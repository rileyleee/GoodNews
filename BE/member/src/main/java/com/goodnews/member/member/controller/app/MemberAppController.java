package com.goodnews.member.member.controller.app;

import com.goodnews.member.common.dto.BaseResponseDto;
import com.goodnews.member.common.dto.LoginDto;
import com.goodnews.member.member.dto.request.member.MemberInfoUpdateRequestDto;
import com.goodnews.member.member.dto.request.member.MemberLoginAdminRequestDto;
import com.goodnews.member.member.dto.request.member.MemberRegistRequestDto;
import com.goodnews.member.jwt.JwtTokenProvider;
import com.goodnews.member.member.dto.request.member.MemberFirstLoginRequestDto;
import com.goodnews.member.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Member", description = "멤버 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/member-service/api/members/app")
public class MemberAppController {

    private final JwtTokenProvider jwtTokenProvider;
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




}
