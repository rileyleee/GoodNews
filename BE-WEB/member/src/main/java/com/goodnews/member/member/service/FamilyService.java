package com.goodnews.member.member.service;

import com.goodnews.member.common.dto.BaseResponseDto;
import com.goodnews.member.common.exception.validator.FamilyValidator;
import com.goodnews.member.member.dto.request.family.FamilyPlaceCanuseDto;
import com.goodnews.member.member.dto.request.family.FamilyPlaceUpdateRequestDto;
import com.goodnews.member.member.dto.request.family.FamilyRegistRequestDto;
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
import com.goodnews.member.member.dto.request.family.FamilyRegistPlaceRequestDto;
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
        Optional<Member> findMemberPhone = memberRepository.findByPhoneNumber(memberRegistFamilyRequestDto.getOtherPhone());
        memberValidator.checkMember(findMemberPhone,memberRegistFamilyRequestDto.getOtherPhone());

        Optional<FamilyMember> findFamilyMember = familyMemberRepository.findByMemberIdAndFamilyFamilyId(findMemberPhone.get().getId(), memberRegistFamilyRequestDto.getMemberId());
        familyValidator.checkRegistFamily(findFamilyMember, findMemberPhone.get().getId());

        Optional<Family> findFamilyInfo = familyRepository.findById(memberRegistFamilyRequestDto.getMemberId());

        Optional<FamilyMember> findFamilyOther = familyMemberRepository.findByMemberId(memberRegistFamilyRequestDto.getMemberId());
        familyValidator.checkRegistOtherFamily(findFamilyInfo,findFamilyOther, memberRegistFamilyRequestDto.getMemberId());

        Optional<Member> findMember = memberRepository.findById(memberRegistFamilyRequestDto.getMemberId());
        memberValidator.checkMember(findMember, memberRegistFamilyRequestDto.getMemberId());

        Optional<Family> findFamily = familyRepository.findById(findMember.get().getId());

        Optional<Member> findOther = memberRepository.findById(findMemberPhone.get().getId());
        memberValidator.checkMember(findOther, findMemberPhone.get().getId());
        if (findFamily.isEmpty()) {
            Family saveFamily = familyRepository.save(Family.builder()
                    .member(findMember.get())
                    .build());
            FamilyMember saveFamilyMember = familyMemberRepository.save(FamilyMember.builder()
                    .family(saveFamily)
                    .member(findOther.get())
                    .build());

            familyMemberRepository.save(FamilyMember.builder()
                    .family(saveFamily)
                    .member(findMember.get())
                    .build()).updateApprove(true);
            return BaseResponseDto
                    .builder().success(true)
                    .message("가족 신청 요청을 성공했습니다")
                    .data(MemberRegistFamilyResposneDto.builder()
                            .familyId(saveFamilyMember.getFamily().getFamilyId()).build())
                    .build();
        }
        else{
            FamilyMember saveFamilyMember = familyMemberRepository.save(FamilyMember.builder()
                    .family(findFamily.get())
                    .member(findOther.get())
                    .build());
            return BaseResponseDto
                    .builder().success(true)
                    .message("가족 신청 요청을 성공했습니다")
                    .data(MemberRegistFamilyResposneDto.builder()
                            .familyId(saveFamilyMember.getFamily().getFamilyId()).build())
                    .build();
        }
    }

    @Transactional
    public BaseResponseDto updateFamilyMember(FamilyRegistRequestDto familyRegistRequestDto) {
//        Optional<FamilyMember> familyMember = memberQueryDslRepository.findFamilyMember(familyRegistRequestDto.getMemberId());
        Optional<FamilyMember> familyMember = familyMemberRepository.findById(familyRegistRequestDto.getFamilyMemberId());
        familyValidator.checkUpdateFamily(familyMember);

        if (!familyRegistRequestDto.getRefuse()) {
            familyMember.get().updateApprove(true);
            return BaseResponseDto.builder()
                    .success(true)
                    .message("가족 신청을 수락하셨습니다")
                    .build();
        }
        familyMemberRepository.delete(familyMember.get());
        return BaseResponseDto.builder()
                .success(true)
                .message("가족 신청을 거절했습니다")
                .build();
    }

    @Transactional(readOnly = true)
    public BaseResponseDto getFamilyMemberInfo(String memberId) {

        Optional<FamilyMember> familyMember = memberQueryDslRepository.findFamilyMember(memberId);

        familyValidator.checkUpdateFamily(familyMember);
        familyValidator.checkApproveFamily(familyMember,memberId);
        List<Member> familyMemberList = memberQueryDslRepository.findFamilyMemberList(familyMember.get().getFamily().getFamilyId(),memberId);
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
                                        .lastConnection(member.getLastConnection().toString())
                                        .state(member.getState())
                                        .familyId(familyMember.get().getFamily().getFamilyId())
                                        .build())
                        .collect(Collectors.toList()))
                .build();
    }
    @Transactional
    public BaseResponseDto registFamilyPlace(FamilyRegistPlaceRequestDto familyRegistPlaceRequestDto) {

        Optional<FamilyMember> familyId = memberQueryDslRepository.findFamilyId(familyRegistPlaceRequestDto.getMemberId());
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
                                .build()))
                        .build())
                .build();



    }

    @Transactional(readOnly = true)
    public BaseResponseDto getFamilyPlaceInfo(String memberId) {

        List<FamilyPlace> aLlFamilyPlace = memberQueryDslRepository.findALlFamilyPlace(memberId);

        familyValidator.checkFamilyPlaceList(aLlFamilyPlace);

        return BaseResponseDto.builder()
                .success(true)
                .message("가족 모임 장소 리스트 조회 성공했습니다")
                .data(aLlFamilyPlace.stream()
                        .map(familyPlace ->
                                FamilyPlaceInfoResponseDto.builder()
                                        .placeId(familyPlace.getId())
                                        .name(familyPlace.getName())
                                        .canuse(familyPlace.isCanuse())
                                        .seq(familyPlace.getSeq())
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
            Optional<Member> findMember = memberRepository.findById(familyMember.getFamily().getFamilyId());

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
}
