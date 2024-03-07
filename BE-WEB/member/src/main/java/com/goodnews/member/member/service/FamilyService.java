package com.goodnews.member.member.service;

import com.goodnews.member.common.dto.BaseResponseDto;
import com.goodnews.member.common.exception.validator.FamilyValidator;
import com.goodnews.member.member.dto.request.family.*;
import com.goodnews.member.member.dto.response.family.FamilyInviteResponseDto;
import com.goodnews.member.member.dto.response.family.FamilyPlaceDetailResponseDto;
import com.goodnews.member.member.dto.response.family.FamilyPlaceInfoResponseDto;
import com.goodnews.member.member.dto.response.family.FamilyRegistPlaceResponseDto;
import com.goodnews.member.member.dto.response.member.MemberResponseDto;
import com.goodnews.member.common.exception.validator.MemberValidator;
import com.goodnews.member.member.domain.Family;
import com.goodnews.member.member.domain.FamilyMember;
import com.goodnews.member.member.domain.FamilyPlace;
import com.goodnews.member.member.domain.Member;
import com.goodnews.member.member.dto.request.member.MemberFirstLoginRequestDto;
import com.goodnews.member.member.dto.request.member.MemberRegistFamilyRequestDto;
import com.goodnews.member.member.dto.response.member.MemberRegistFamilyResposneDto;
import com.goodnews.member.member.repository.FamilyMemberRepository;
import com.goodnews.member.member.repository.FamilyPlaceRepository;
import com.goodnews.member.member.repository.FamilyRepository;
import com.goodnews.member.member.repository.MemberRepository;
import com.goodnews.member.member.repository.querydsl.MemberQueryDslRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FamilyService {
    private final MemberRepository memberRepository;
    private final MemberValidator memberValidator;
    private final FamilyRepository familyRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final MemberQueryDslRepository memberQueryDslRepository;
    private final FamilyValidator familyValidator;
    private final FamilyPlaceRepository familyPlaceRepository;

    @Transactional
    public BaseResponseDto registFamily(MemberRegistFamilyRequestDto memberRegistFamilyRequestDto) {
        // 등록된 사용자인지 확인
        Optional<Member> findMemberSender = memberRepository.findById(memberRegistFamilyRequestDto.getMemberId());
        memberValidator.checkMember(findMemberSender, memberRegistFamilyRequestDto.getOtherPhone());

        Optional<Member> findMemberReceiver = memberRepository.findByPhoneNumber(memberRegistFamilyRequestDto.getOtherPhone());
        memberValidator.checkMember(findMemberReceiver, memberRegistFamilyRequestDto.getMemberId());

        // 이미 가족인지 확인
        familyValidator.checkSameFamily(findMemberSender, findMemberReceiver);

        Family saveFamily = familyRepository.save(new Family());

        familyMemberRepository.save(FamilyMember.builder()
                .sendMember(findMemberSender.get().getId())
                .member(findMemberReceiver.get())
                .family(saveFamily)
                .build());
        return BaseResponseDto
                .builder().success(true)
                .message("가족 신청 요청을 성공했습니다")
                .data(MemberRegistFamilyResposneDto.builder()
                        .familyId(saveFamily.getFamilyId()).build())
                .build();
    }

    @Transactional
    public BaseResponseDto updateFamilyMember(FamilyRegistRequestDto familyRegistRequestDto) {
        // 가족 신청 수락
        Optional<FamilyMember> familyInfo = familyMemberRepository.findById(familyRegistRequestDto.getFamilyMemberId());

        if(familyRegistRequestDto.getRefuse()){
            // 거절 했을 때
            familyRepository.delete(familyInfo.get().getFamily());
            familyMemberRepository.delete(familyInfo.get());

            return BaseResponseDto.builder()
                    .success(true)
                    .message("가족 신청을 거절했습니다")
                    .build();
        }else{

            //사람 존재 여부도 확인해야 함.

            // 수락 했을 때
            // 각각의 가족 Id를 가진 것을 가져온다
            Optional<FamilyMember> findFamilySender= familyMemberRepository.findByMemberIdAndApproveIsTrue(familyInfo.get().getSendMember());
            Optional<FamilyMember> findFamilyReceiver = familyMemberRepository.findByMemberIdAndApproveIsTrue(familyInfo.get().getMember().getId());


            if(findFamilySender.isEmpty() && findFamilyReceiver.isEmpty()){
                // 1. 둘다 가족이 존재하지 않을 경우
                // receiver
                familyInfo.get().updateApprove();
                Optional<Member> findSender = memberRepository.findById(familyInfo.get().getSendMember());

                // sender
                familyMemberRepository.save(FamilyMember.builder()
                        .sendMember(familyInfo.get().getSendMember())
                        .member(findSender.get())
                        .family(familyInfo.get().getFamily())
                        .build()).updateApprove();

                // 가족 정보 넣어 주기
                memberRepository.findById(familyInfo.get().getSendMember()).get().updateFamily(familyInfo.get().getFamily());
                memberRepository.findById(familyInfo.get().getMember().getId()).get().updateFamily(familyInfo.get().getFamily());

            }else if(findFamilySender.isPresent() && findFamilyReceiver.isPresent()){
                // 2. 가족이 모두 존재할 경우 -> 보낸쪽으로 합치기
                // 요청 받은 가족의 장소를 모두 제거하고
                List<FamilyPlace> findFamilyPlace = familyPlaceRepository.findByFamilyFamilyId(findFamilyReceiver.get().getFamily().getFamilyId());
                familyPlaceRepository.deleteAll(findFamilyPlace);
                // 멤버 요청 받은 가족의 family를 모두 변경
                List<Member> findReceiverMember = memberRepository.findByFamilyFamilyId(findFamilyReceiver.get().getFamily().getFamilyId());
                // 가족의 id 도 모두 변경
                List<FamilyMember> findReceiverFamily = familyMemberRepository.findByFamilyFamilyId(findFamilyReceiver.get().getFamily().getFamilyId());
                // 받은 가족 id 삭제 하기
                familyMemberRepository.delete(familyInfo.get());
                familyRepository.delete(familyInfo.get().getFamily());
                familyRepository.delete(findFamilyReceiver.get().getFamily());

                findReceiverMember.forEach( receiver -> {
                    receiver.updateFamily(findFamilySender.get().getFamily());
                });

                findReceiverFamily.forEach( receiver -> {
                    receiver.updateFamily(findFamilySender.get().getFamily());
                });


            }else if(findFamilyReceiver.isEmpty()){
                Family prev = familyInfo.get().getFamily();
                // 3. 요청 보낸 사람이 가족이 있을 경우
                familyInfo.get().updateFamily(findFamilySender.get().getFamily());
                familyInfo.get().updateApprove();

                // 가족 정보 넣어 주기
                memberRepository.findById(familyInfo.get().getMember().getId()).get().updateFamily(findFamilySender.get().getFamily());

                familyRepository.delete(prev);

            }else {
                Optional<Member> findSender = memberRepository.findById(familyInfo.get().getSendMember());
                // 4. 요청 받은 사람이 가족이 있을 경우
                familyMemberRepository.save(FamilyMember.builder()
                        .sendMember(findFamilyReceiver.get().getMember().getId())
                        .member(findSender.get())
                        .family(findFamilyReceiver.get().getFamily())
                        .build()).updateApprove();

                // 가족 정보 넣어 주기
                memberRepository.findById(familyInfo.get().getSendMember()).get().updateFamily(findFamilyReceiver.get().getFamily());

                familyMemberRepository.delete(familyInfo.get());
                familyRepository.delete(familyInfo.get().getFamily());
            }

            return BaseResponseDto.builder()
                    .success(true)
                    .message("가족 신청을 수락하셨습니다")
                    .build();
        }
    }

    @Transactional(readOnly = true)
    public BaseResponseDto getFamilyMemberInfo(String memberId) {

        Optional<FamilyMember> familyMember = familyMemberRepository.findByMemberIdAndApproveIsTrue(memberId);
        familyValidator.checkUpdateFamily(familyMember);

        List<Member> familyMemberList = memberQueryDslRepository.findFamilyMemberList(familyMember.get().getFamily().getFamilyId(), memberId);
        if (familyMemberList.isEmpty()) {
            return BaseResponseDto.builder()
                    .success(true)
                    .message("가족 구성원이 존재하지 않습니다")
                    .build();
        }
        return BaseResponseDto.builder()
                .success(true)
                .message("가족 구성원 정보를 조회 성공하셨습니다")
                .data(familyMemberList.stream()
                        .map(member ->
                                MemberResponseDto.builder()
                                        .memberId(member.getId())
                                        .name(member.getName())
                                        .phoneNumber(member.getPhoneNumber())
                                        .lastConnection(member.getLastConnection().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))
                                        .state(member.getState())
                                        .familyId(familyMember.get().getFamily().getFamilyId())
                                        .build())
                        .collect(Collectors.toList()))
                .build();
    }
    @Transactional
    public BaseResponseDto registFamilyPlace(FamilyRegistPlaceRequestDto familyRegistPlaceRequestDto) {

        Optional<FamilyMember> familyId = familyMemberRepository.findByMemberIdAndApproveIsTrue(familyRegistPlaceRequestDto.getMemberId()) ;
        familyValidator.checkFamily(familyId,familyRegistPlaceRequestDto.getMemberId());

        List<FamilyPlace> findFamilyPlace = familyPlaceRepository.findByFamilyFamilyId(familyId.get().getFamily().getFamilyId());
        familyValidator.checkSeq(familyRegistPlaceRequestDto.getSeq(), familyRegistPlaceRequestDto.getMemberId());
        familyValidator.checkFamilyPlace(findFamilyPlace);

        return BaseResponseDto.builder()
                .success(true)
                .message("가족 모임 장소 동록했습니다")
                .data(FamilyRegistPlaceResponseDto.builder()
                        .familyPlace(familyPlaceRepository.save(FamilyPlace.builder()
                                .name(familyRegistPlaceRequestDto.getName())
                                .lat(familyRegistPlaceRequestDto.getLat())
                                .lon(familyRegistPlaceRequestDto.getLon())
                                .canuse(true)
                                .family(familyId.get().getFamily())
                                .seq(familyRegistPlaceRequestDto.getSeq())
                                .address(familyRegistPlaceRequestDto.getAddress())
                                .registerUser(familyRegistPlaceRequestDto.getRegisterUser())
                                .build()))
                        .build())
                .build();
    }

    @Transactional(readOnly = true)
    public BaseResponseDto getFamilyPlaceInfo(String memberId) {
        // 가족 여부 확인
        Optional<FamilyMember> familyMember = familyMemberRepository.findByMemberIdAndApproveIsTrue(memberId) ;
        familyValidator.checkFamily(familyMember, memberId);

        // 가족 장소 확인
        List<FamilyPlace> allFamilyPlace = familyPlaceRepository.findByFamilyFamilyId(familyMember.get().getFamily().getFamilyId());
        familyValidator.checkFamilyPlaceList(allFamilyPlace);

        return BaseResponseDto.builder()
                .success(true)
                .message("가족 모임 장소 리스트 조회 성공했습니다")
                .data(allFamilyPlace.stream()
                        .map(familyPlace ->
                                FamilyPlaceInfoResponseDto.builder()
                                        .placeId(familyPlace.getId())
                                        .name(familyPlace.getName())
                                        .canuse(familyPlace.isCanuse())
                                        .seq(familyPlace.getSeq())
                                        .createdDate(familyPlace.createdDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))
                                        .build())
                        .collect(Collectors.toList())).build();
    }

    @Transactional(readOnly = true)
    public BaseResponseDto getFamilyPlaceInfoDetail(int placeId) {

        Optional<FamilyPlace> findPlace = familyPlaceRepository.findById(placeId);
        familyValidator.checkFamilyPlace(findPlace);

        return BaseResponseDto.builder()
                .success(true)
                .message("가족 모임 상세 정보 조회를 성공했습니다")
                .data(FamilyPlaceDetailResponseDto.builder()
                        .familyPlace(findPlace.get())
                        .build())
                .build();
    }


    @Transactional
    public BaseResponseDto getFamilyPlaceInfoUpdate(int placeId, FamilyPlaceUpdateRequestDto familyPlaceUpdateRequestDto) {

        Optional<FamilyPlace> findPlace = familyPlaceRepository.findById(placeId);
        familyValidator.checkFamilyPlace(findPlace);

        findPlace.get().updatePlaceInfo(familyPlaceUpdateRequestDto);


        return BaseResponseDto.builder()
                .message("가족 모임 장소 수정을 성공했습니다")
                .build();
    }

    @Transactional
    public BaseResponseDto getFamilyPlaceInfoCanUseUpdate(int placeId, FamilyPlaceCanuseDto familyPlaceCanuseDto) {
        Optional<FamilyPlace> findPlace = familyPlaceRepository.findById(placeId);
        familyValidator.checkFamilyPlace(findPlace);

        findPlace.get().updatePlaceCanuse(familyPlaceCanuseDto);
        return BaseResponseDto.builder()
                .success(true)
                .message("가족 모임 장소 사용 가능 여부를 수정했습니다")
                .build();
    }

    @Transactional(readOnly = true)
    public BaseResponseDto getRegistFamily(MemberFirstLoginRequestDto memberFirstLoginRequestDto) {


        List<FamilyInviteResponseDto> responseDtoList = new ArrayList<>();

        List<FamilyMember> findFamilyMember = familyMemberRepository.findByMemberIdAndApproveIsFalse(memberFirstLoginRequestDto.getMemberId());

        for (FamilyMember familyMember : findFamilyMember) {
            Optional<Member> findMember = memberRepository.findById(familyMember.getMember().getId());

            findMember.ifPresent(member -> {
                // 여기서 FamilyInviteResponseDto를 생성하고 필요한 정보를 설정한다.
                FamilyInviteResponseDto responseDto = FamilyInviteResponseDto.builder()
                        .id(familyMember.getId())
                        .name(member.getName())
                        .phoneNumber(member.getPhoneNumber())
                        .build();

                // 리스트에 추가한다.
                responseDtoList.add(responseDto);
            });
        }

        return BaseResponseDto.builder()
                .success(true)
                .message("가족 수락 요청 리스트를 조회했습니다")
                .data(responseDtoList)
                .build();
    }

    @Transactional
    public BaseResponseDto leaveFamily(MemberFirstLoginRequestDto memberFirstLoginRequestDto){
        Optional<FamilyMember> familyMember = familyMemberRepository.findByMemberIdAndApproveIsTrue(memberFirstLoginRequestDto.getMemberId()) ;
        familyValidator.checkFamily(familyMember, memberFirstLoginRequestDto.getMemberId());

        List<FamilyMember> familyMemberList = familyMemberRepository.findByFamilyFamilyId(familyMember.get().getFamily().getFamilyId());

        if(familyMemberList.size() > 2){
            // 멤버 정보 family 삭제
            familyMember.get().getMember().updateFamily(null);
            // 가족 리스트 삭제
            familyMemberRepository.delete(familyMember.get());

        }else{
            Family family = familyMember.get().getFamily();
            // 가족 장소 모두 삭제
            List<FamilyPlace> familyPlaceList = familyPlaceRepository.findByFamilyFamilyId(familyMember.get().getFamily().getFamilyId());
            familyPlaceRepository.deleteAll(familyPlaceList);

            List<Member> memberList = memberRepository.findByFamilyFamilyId(family.getFamilyId());
            // 멤버 정보 family 삭제
            memberList.forEach(member -> {
                member.updateFamily(null);
            });

            // 가족 리스트 모두 삭제
            familyMemberRepository.deleteAll(familyMemberList);

            // 가족 삭제
            familyRepository.delete(family);
        }
        return BaseResponseDto.builder()
                .success(true)
                .message("가족 탈퇴를 완료했습니다.")
                .build();
    }


    @Transactional
    public BaseResponseDto deleteFamilyPlace(FamilyPlaceRequestDto familyPlaceRequestDto){
        Optional<FamilyPlace> familyPlace = familyPlaceRepository.findById(familyPlaceRequestDto.getPlaceId());
        familyValidator.checkFamilyPlace(familyPlace);

        familyPlaceRepository.delete(familyPlace.get());

        return BaseResponseDto.builder()
                .success(true)
                .message("가족 장소 삭제를 완료했습니다.")
                .build();
    }
}
