package com.ssafy.goodnews.member.service;

import com.ssafy.goodnews.common.domain.BaseEntity;
import com.ssafy.goodnews.common.dto.BaseResponseDto;
import com.ssafy.goodnews.common.exception.validator.FamilyValidator;
import com.ssafy.goodnews.common.exception.validator.MemberValidator;
import com.ssafy.goodnews.member.domain.Family;
import com.ssafy.goodnews.member.domain.FamilyMember;
import com.ssafy.goodnews.member.domain.FamilyPlace;
import com.ssafy.goodnews.member.domain.Member;
import com.ssafy.goodnews.member.dto.request.FamilyRegistPlaceRequestDto;
import com.ssafy.goodnews.member.dto.request.MemberFirstLoginRequestDto;
import com.ssafy.goodnews.member.dto.request.MemberRegistFamilyRequestDto;
import com.ssafy.goodnews.member.dto.response.*;
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
            saveFamilyMember.updateApprove(true);
            familyMemberRepository.save(FamilyMember.builder()
                    .family(saveFamily)
                    .member(findMember.get())
                    .build());
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
        familyValidator.checkRegistFamily(familyMember, memberFirstLoginRequestDto.getMemberId());
        familyMember.get().updateApprove(true);
        return BaseResponseDto.builder()
                .success(true)
                .message("가족 신청을 수락하셨습니다")
                .build();
    }

    @Transactional(readOnly = true)
    public BaseResponseDto getFamilyMemberInfo(String memberId) {

        Optional<FamilyMember> familyMember = memberQueryDslRepository.findFamilyMember(memberId);
        familyValidator.checkRegistFamily(familyMember, memberId);
        List<Member> familyMemberList = memberQueryDslRepository.findFamilyMemberList(familyMember.get().getFamily().getFamilyId(),memberId);

        return BaseResponseDto.builder()
                .success(true)
                .message("가족 구성원 정보를 조회 성공하셨습니다")
                .data(familyMemberList.stream()
                        .map(member ->
                                MemberResponseDto.builder()
                                        .memberId(member.getId())
                                        .name(member.getName())
                                        .lastConnection(member.getLastConnection().toString())
                                        .build())
                        .collect(Collectors.toList()))
                .build();
    }
    @Transactional
    public BaseResponseDto registFamilyPlace(FamilyRegistPlaceRequestDto familyRegistPlaceRequestDto) {

        Optional<Family> familyId = memberQueryDslRepository.findFamilyId(familyRegistPlaceRequestDto.getMemberId());
        familyValidator.checkFamily(familyId,familyRegistPlaceRequestDto.getMemberId());
        FamilyPlace newFamilyPlace = familyPlaceRepository.save(FamilyPlace.builder()
                .name(familyRegistPlaceRequestDto.getName())
                .lat(familyRegistPlaceRequestDto.getLat())
                .lon(familyRegistPlaceRequestDto.getLon())
                .canuse(true)
                .family(familyId.get())
                .build());


        return BaseResponseDto.builder()
                .success(true)
                .message("가족 모임 장소 동록했습니다")
                .data(FamilyRegistPlaceResponseDto.builder()
                        .familyPlace(newFamilyPlace)
                        .build())
                .build();
    }

    @Transactional(readOnly = true)
    public BaseResponseDto getFamilyPlaceInfo(String memberId) {

        List<FamilyPlace> aLlFamilyPlace = memberQueryDslRepository.findALlFamilyPlace(memberId);

        familyValidator.checkFamilyPlaceList(aLlFamilyPlace);

        return BaseResponseDto.builder()
                .message("가족 모임 장소 리스트 조회 성공했습니다")
                .data(aLlFamilyPlace.stream()
                        .map(familyPlace ->
                                FamilyPlaceInfoResponseDto.builder()
                                        .placeId(familyPlace.getId())
                                        .name(familyPlace.getName())
                                        .canuse(familyPlace.isCanuse())
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



}
