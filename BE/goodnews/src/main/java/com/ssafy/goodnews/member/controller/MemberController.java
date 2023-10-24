package com.ssafy.goodnews.member.controller;

import com.ssafy.goodnews.common.dto.BaseResponseDto;
import com.ssafy.goodnews.member.dto.request.MemberFirstLoginRequestDto;
import com.ssafy.goodnews.member.dto.request.MemberInfoUpdateRequestDto;
import com.ssafy.goodnews.member.dto.request.MemberRegistFamilyRequestDto;
import com.ssafy.goodnews.member.dto.request.MemberRegistRequestDto;
import com.ssafy.goodnews.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Member", description = "멤버 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "추가 정보 등록", description = "멤버 추가 정보(전화번호,이름,생년월일,성별,혈액형,특이사항) 등록")
    @PostMapping("/registinfo")
    private BaseResponseDto registMemberInfo(@RequestBody MemberRegistRequestDto memberRegistRequestDto) {


        return memberService.registMemberInfo(memberRegistRequestDto);
    }

    @Operation(summary = "최초 로그인 유무 조회", description = "최초로그인 조회")
    @PostMapping("/firstlogin")
    private BaseResponseDto firstLoginInfo(@RequestBody MemberFirstLoginRequestDto memberFirstLoginRequestDto) {


        return memberService.firstLoginSearch(memberFirstLoginRequestDto);
    }
    @Operation(summary = "멤버 정보 수정", description = "멤버 정보(이름,생년월일,혈액형,특이사항) 수정")
    @PutMapping("/{memberId}")
    private BaseResponseDto updateMemberInfo(@PathVariable String memberId, @RequestBody MemberInfoUpdateRequestDto memberInfoUpdateRequestDto) {


        return memberService.updateMemberInfo(memberId,memberInfoUpdateRequestDto);
    }

    @Operation(summary = "멤버 정보 조회", description = "멤버 정보(전화번호,이름,생년월일,성별,혈액형,특이사항) 조회")
    @PostMapping("/search")
    private BaseResponseDto findMemberInfo(@RequestBody MemberFirstLoginRequestDto memberFirstLoginRequestDto) {

        return memberService.getMemberInfo(memberFirstLoginRequestDto.getMemberId());
    }

    @Operation(summary = "가족 신청 요청", description = "가족 신청 요청하기 familyId는 상대방 전화번호")
    @PostMapping("/registfamily")
    private BaseResponseDto registFamily(@RequestBody MemberRegistFamilyRequestDto memberRegistFamilyRequestDto) {

        return memberService.registFamily(memberRegistFamilyRequestDto);
    }


    @Operation(summary = "가족 신청 수락", description = "각족 신청 수락 approve 수정")
    @PutMapping("/acceptfamily")
    private BaseResponseDto updateRegistFamily(@RequestBody MemberFirstLoginRequestDto memberFirstLoginRequestDto) {


        return memberService.updateFamilyMember(memberFirstLoginRequestDto);
    }

}
