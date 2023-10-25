package com.ssafy.goodnews.member.service;

import com.ssafy.goodnews.common.dto.BaseResponseDto;
import com.ssafy.goodnews.common.exception.validator.FamilyValidator;
import com.ssafy.goodnews.common.exception.validator.MemberValidator;
import com.ssafy.goodnews.member.domain.Family;
import com.ssafy.goodnews.member.domain.FamilyMember;
import com.ssafy.goodnews.member.domain.Member;
import com.ssafy.goodnews.member.dto.request.MemberFirstLoginRequestDto;
import com.ssafy.goodnews.member.dto.request.MemberRegistFamilyRequestDto;
import com.ssafy.goodnews.member.dto.response.MemberRegistFamilyResposneDto;
import com.ssafy.goodnews.member.dto.response.MemberResponseDto;
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
        familyValidator.checkFamilyMember(familyMember, memberFirstLoginRequestDto.getMemberId());
        familyMember.get().updateApprove(true);
        return BaseResponseDto.builder()
                .success(true)
                .message("가족 신청을 수락하셨습니다")
                .build();
    }

    @Transactional(readOnly = true)
    public BaseResponseDto getFamilyMemberInfo(String memberId) {

        Optional<FamilyMember> familyMember = memberQueryDslRepository.findFamilyMember(memberId);
        familyValidator.checkFamilyMember(familyMember, memberId);
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
}
