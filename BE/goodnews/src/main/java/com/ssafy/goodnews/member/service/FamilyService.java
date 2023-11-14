package com.ssafy.goodnews.member.service;

import com.ssafy.goodnews.common.dto.BaseResponseDto;
import com.ssafy.goodnews.common.exception.validator.FamilyValidator;
import com.ssafy.goodnews.common.exception.validator.MemberValidator;
import com.ssafy.goodnews.member.domain.Family;
import com.ssafy.goodnews.member.domain.FamilyMember;
import com.ssafy.goodnews.member.domain.FamilyPlace;
import com.ssafy.goodnews.member.domain.Member;
import com.ssafy.goodnews.member.dto.request.family.FamilyPlaceCanuseDto;
import com.ssafy.goodnews.member.dto.request.family.FamilyPlaceUpdateRequestDto;
import com.ssafy.goodnews.member.dto.request.family.FamilyRegistPlaceRequestDto;
import com.ssafy.goodnews.member.dto.request.member.MemberFirstLoginRequestDto;
import com.ssafy.goodnews.member.dto.request.member.MemberRegistFamilyRequestDto;
import com.ssafy.goodnews.member.dto.response.family.FamilyPlaceDetailResponseDto;
import com.ssafy.goodnews.member.dto.response.family.FamilyPlaceInfoResponseDto;
import com.ssafy.goodnews.member.dto.response.family.FamilyRegistPlaceResponseDto;
import com.ssafy.goodnews.member.dto.response.member.MemberRegistFamilyResposneDto;
import com.ssafy.goodnews.member.dto.response.member.MemberResponseDto;
import com.ssafy.goodnews.member.repository.FamilyMemberRepository;
import com.ssafy.goodnews.member.repository.FamilyPlaceRepository;
import com.ssafy.goodnews.member.repository.FamilyRepository;
import com.ssafy.goodnews.member.repository.MemberRepository;
import com.ssafy.goodnews.member.repository.querydsl.MemberQueryDslRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        Optional<FamilyMember> findFamilyMember = familyMemberRepository.findByMemberIdAndFamilyFamilyId(memberRegistFamilyRequestDto.getFamilyId(), memberRegistFamilyRequestDto.getMemberId());
        familyValidator.checkRegistFamily(findFamilyMember, memberRegistFamilyRequestDto.getFamilyId());

        Optional<Family> findFamilyInfo = familyRepository.findById(memberRegistFamilyRequestDto.getMemberId());

        Optional<FamilyMember> findFamilyOther = familyMemberRepository.findByMemberId(memberRegistFamilyRequestDto.getMemberId());
        familyValidator.checkRegistOtherFamily(findFamilyInfo,findFamilyOther, memberRegistFamilyRequestDto.getMemberId());

        Optional<Member> findMember = memberRepository.findById(memberRegistFamilyRequestDto.getMemberId());
        memberValidator.checkMember(findMember, memberRegistFamilyRequestDto.getMemberId());

        Optional<Family> findFamily = familyRepository.findById(findMember.get().getId());

        Optional<Member> findOther = memberRepository.findById(memberRegistFamilyRequestDto.getFamilyId());
        memberValidator.checkMember(findOther, memberRegistFamilyRequestDto.getFamilyId());
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
    public BaseResponseDto updateFamilyMember(MemberFirstLoginRequestDto memberFirstLoginRequestDto) {
        Optional<FamilyMember> familyMember = memberQueryDslRepository.findFamilyMember(memberFirstLoginRequestDto.getMemberId());
        familyValidator.checkUpdateFamily(familyMember, memberFirstLoginRequestDto.getMemberId());
        familyMember.get().updateApprove(true);
        return BaseResponseDto.builder()
                .success(true)
                .message("가족 신청을 수락하셨습니다")
                .build();
    }

    @Transactional(readOnly = true)
    public BaseResponseDto getFamilyMemberInfo(String memberId) {

        Optional<FamilyMember> familyMember = memberQueryDslRepository.findFamilyMember(memberId);

        familyValidator.checkUpdateFamily(familyMember, memberId);
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
        System.out.println("familyId.get().getFamilyId() = " + familyId.get().getFamily().getFamilyId());
        familyValidator.checkFamily(familyId,familyRegistPlaceRequestDto.getMemberId());
        List<FamilyPlace> findFamilyPlace = familyPlaceRepository.findByFamilyFamilyId(familyId.get().getFamily().getFamilyId());

        if (findFamilyPlace.isEmpty()) {
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
                                    .build()))
                            .build())
                    .build();
        }
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
                                .seq(findFamilyPlace.get(findFamilyPlace.size()-1).getSeq())
                                .build()))
                        .build())
                .build();

//        return null;
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
}
