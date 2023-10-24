package com.ssafy.goodnews.member.service;

import com.ssafy.goodnews.common.dto.BaseResponseDto;
import com.ssafy.goodnews.common.exception.validator.FamilyValidator;
import com.ssafy.goodnews.common.exception.validator.MemberValidator;
import com.ssafy.goodnews.member.domain.Family;
import com.ssafy.goodnews.member.domain.FamilyMember;
import com.ssafy.goodnews.member.domain.Member;
import com.ssafy.goodnews.member.dto.request.MemberFirstLoginRequestDto;
import com.ssafy.goodnews.member.dto.request.MemberInfoUpdateRequestDto;
import com.ssafy.goodnews.member.dto.request.MemberRegistFamilyRequestDto;
import com.ssafy.goodnews.member.dto.request.MemberRegistRequestDto;
import com.ssafy.goodnews.member.dto.response.MemberFirstLoginResponseDto;
import com.ssafy.goodnews.member.dto.response.MemberInfoResponseDto;
import com.ssafy.goodnews.member.dto.response.MemberRegistFamilyResposneDto;
import com.ssafy.goodnews.member.repository.FamilyMemberRepository;
import com.ssafy.goodnews.member.repository.FamilyPlaceRepository;
import com.ssafy.goodnews.member.repository.FamilyRepository;
import com.ssafy.goodnews.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {


    private final MemberRepository memberRepository;
    private final FamilyValidator familyValidator;
    private final MemberValidator memberValidator;
    private final FamilyRepository familyRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final FamilyPlaceRepository familyPlaceRepository;
    @Transactional
    public BaseResponseDto registMemberInfo(MemberRegistRequestDto memberRegistRequestDto) {

        Member newMember = Member.builder()
                .id(memberRegistRequestDto.getMemberId())
                .name(memberRegistRequestDto.getName())
                .birthDate(memberRegistRequestDto.getBirthDate())
                .gender(memberRegistRequestDto.getGender())
                .bloodType(memberRegistRequestDto.getBloodType())
                .addInfo(memberRegistRequestDto.getAddInfo())
                .build();

        memberRepository.save(newMember);


        return BaseResponseDto.builder()
                .success(true)
                .message("추가 정보 등록 성공했습니다")
                .data(newMember)
                .build();
    }

    @Transactional(readOnly = true)
    public BaseResponseDto firstLoginSearch(MemberFirstLoginRequestDto memberFirstLoginRequestDto) {

        Optional<Member> findMember = memberRepository.findById(memberFirstLoginRequestDto.getMemberId());


        if (findMember.isPresent()) {
            return BaseResponseDto.builder()
                    .success(true)
                    .message("최초 로그인 사용 여부 조회 성공했습니다")
                    .data(MemberFirstLoginResponseDto.builder()
                            .firstLogin(true)
                            .build())
                    .build();
        } else {
            return BaseResponseDto.builder()
                    .success(true)
                    .message("최초 로그인 사용 여부 조회 성공했습니다")
                    .data(MemberFirstLoginResponseDto.builder()
                            .firstLogin(false)
                            .build())
                    .build();
        }

    }
    @Transactional
    public BaseResponseDto updateMemberInfo(String memberId, MemberInfoUpdateRequestDto memberInfoUpdateRequestDto) {

        Optional<Member> findMember = memberRepository.findById(memberId);

        memberValidator.checkMember(findMember,memberId);

        findMember.get().updateMemberInfo(memberInfoUpdateRequestDto);

        return BaseResponseDto.builder()
                .success(true)
                .message("회원 정보 수정 성공했습니다")
                .build();
    }

    @Transactional(readOnly = true)
    public BaseResponseDto getMemberInfo(String memberId) {
        Optional<Member> findMember = memberRepository.findById(memberId);

        memberValidator.checkMember(findMember, memberId);

        return BaseResponseDto.builder()
                .success(true)
                .message("회원 정보 조회를 성공했습니다")
                .data(MemberInfoResponseDto.builder()
                        .member(findMember.get())
                        .build()
                ).build();
    }

    @Transactional
    public BaseResponseDto registFamily(MemberRegistFamilyRequestDto memberRegistFamilyRequestDto) {

        Optional<FamilyMember> findFamilyMember = familyMemberRepository.findByMemberIdAndFamilyFamilyId(memberRegistFamilyRequestDto.getMemberId(), memberRegistFamilyRequestDto.getFamilyId());
        familyValidator.checkRegistFamily(findFamilyMember, memberRegistFamilyRequestDto.getFamilyId());
        Optional<FamilyMember> findFamilyOther = familyMemberRepository.findByMemberId(memberRegistFamilyRequestDto.getMemberId());
        familyValidator.checkRegistOtherFamily(findFamilyOther, memberRegistFamilyRequestDto.getMemberId());
        Optional<Member> findMember = memberRepository.findById(memberRegistFamilyRequestDto.getMemberId());
        memberValidator.checkMember(findMember, memberRegistFamilyRequestDto.getMemberId());

        Optional<Family> findFamily = familyRepository.findById(findMember.get().getId());

        Optional<Member> findOther = memberRepository.findById(memberRegistFamilyRequestDto.getFamilyId());
        memberValidator.checkMember(findOther, memberRegistFamilyRequestDto.getFamilyId());
        if (findFamily.isEmpty()) {
            Family saveFamily = familyRepository.save(Family.builder()
                    .member(findMember.get())
                    .build());
            System.out.println("saveFamily = " + saveFamily.getFamilyId());
            FamilyMember saveFamilyMember = familyMemberRepository.save(FamilyMember.builder()
                    .family(saveFamily)
                    .member(findOther.get())
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
}
