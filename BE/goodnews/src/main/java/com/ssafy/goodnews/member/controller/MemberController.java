package com.ssafy.goodnews.member.controller;

import com.ssafy.goodnews.common.dto.BaseResponseDto;
import com.ssafy.goodnews.member.dto.request.MemberFirstLoginRequestDto;
import com.ssafy.goodnews.member.dto.request.MemberInfoUpdateRequestDto;
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


}
