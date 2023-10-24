package com.ssafy.goodnews.member.service;

import com.ssafy.goodnews.common.dto.BaseResponseDto;
import com.ssafy.goodnews.common.exception.validator.MemberValidator;
import com.ssafy.goodnews.member.domain.Member;
import com.ssafy.goodnews.member.dto.request.MemberFirstLoginRequestDto;
import com.ssafy.goodnews.member.dto.request.MemberInfoUpdateRequestDto;
import com.ssafy.goodnews.member.dto.request.MemberRegistRequestDto;
import com.ssafy.goodnews.member.dto.response.MemberFirstLoginResponseDto;
import com.ssafy.goodnews.member.dto.response.MemberInfoResponseDto;
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
    private final MemberValidator memberValidator;
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

}
