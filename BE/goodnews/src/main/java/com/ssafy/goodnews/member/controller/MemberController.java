package com.ssafy.goodnews.member.controller;

import com.ssafy.goodnews.common.dto.BaseResponseDto;
import com.ssafy.goodnews.common.dto.LoginDto;
import com.ssafy.goodnews.common.dto.RefreshTokenResponseDto;
import com.ssafy.goodnews.common.dto.TokenDto;
import com.ssafy.goodnews.jwt.JwtTokenProvider;
import com.ssafy.goodnews.member.dto.request.member.*;
import com.ssafy.goodnews.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@Tag(name = "Member", description = "멤버 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberService memberService;

    @Operation(summary = "추가 정보 등록", description = "멤버 추가 정보(기기번호,전화번호,이름,생년월일,성별,혈액형,특이사항) 등록")
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

    @Operation(summary = "멤버 정보 조회", description = "멤버 정보(전화번호,이름,생년월일,성별,혈액형,특이사항,상태(0:모름,1:건강,2:부싱,3:죽음) 조회")
    @PostMapping("/search")
    private BaseResponseDto findMemberInfo(@RequestBody MemberFirstLoginRequestDto memberFirstLoginRequestDto) {

        return memberService.getMemberInfo(memberFirstLoginRequestDto.getMemberId());
    }

    @Operation(summary = "관리자 로그인", description = "관리자 로그인(아이디,비밀번호)")
    @PostMapping("/admin/login")
    private ResponseEntity<BaseResponseDto> loginAdmin(@RequestBody MemberLoginAdminRequestDto memberLoginAdminRequestDto) {

        LoginDto member = memberService.loginAdmin(memberLoginAdminRequestDto);

        String accessToken = jwtTokenProvider.createAccessToken(member.getMemberId());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getMemberId());
        jwtTokenProvider.storeRefreshToken(member.getMemberId(), refreshToken);

        return ResponseEntity.ok()
                .header("Authorization", accessToken)
                .header("Authorization-Refresh", refreshToken)
                .body(BaseResponseDto.builder()
                        .success(true)
                        .message("관리자 로그인을 성공하셨습니다")
                        .build());
    }

    @Operation(summary = "access&refresh 토큰 재발급", description = "access토큰 만료되면 refresh 토큰을 이용하여 재발급하는 API")
    @PostMapping("/reissue")
    public ResponseEntity<RefreshTokenResponseDto> reissue(HttpServletRequest httpServletRequest) {
        String refreshToken = httpServletRequest.getHeader("Authorization-Refresh");

        TokenDto tokenDto = memberService.reissue(refreshToken);


        return  ResponseEntity.ok()
                .header("Authorization", tokenDto.getAccessToken())
                .header("Authorization-Refresh",tokenDto.getRefreshToken())
                .body(RefreshTokenResponseDto.builder()
                        .message("accessToken 과 refreshToken이 재발급 성공하셨습니다")
                        .memberId(tokenDto.getMemberId())
                        .build());
    }

    @Operation(summary = "멤버 상태 정보 수정", description = "멤버 상태(0:모름,1:건강,2:부싱,3:죽음) 수정")
    @PutMapping("/state/{memberId}")
    private BaseResponseDto updateMemberState(@PathVariable String memberId, @RequestBody MemberStateRequestDto memberStateRequestDto) {


        return memberService.updateMemberState(memberId,memberStateRequestDto.getState());
    }

}
