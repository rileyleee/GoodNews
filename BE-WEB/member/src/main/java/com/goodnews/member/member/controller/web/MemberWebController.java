package com.goodnews.member.member.controller.web;

import com.goodnews.member.common.dto.BaseResponseDto;
import com.goodnews.member.common.dto.LoginDto;
import com.goodnews.member.common.dto.RefreshTokenResponseDto;
import com.goodnews.member.common.dto.TokenDto;
import com.goodnews.member.common.exception.validator.TokenValidator;
import com.goodnews.member.jwt.JwtTokenProvider;
import com.goodnews.member.member.domain.Member;
import com.goodnews.member.member.dto.request.facility.MapPopulationRequestDto;
import com.goodnews.member.member.dto.request.member.MemberLoginAdminRequestDto;
import com.goodnews.member.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@Tag(name = "Member", description = "멤버 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/web")
public class MemberWebController {
    private final TokenValidator tokenValidator;
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "관리자 로그인", description = "관리자 로그인(아이디,비밀번호)")
    @PostMapping("/login")
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

    @Operation(summary = "앱 이용자 조회", description = "앱 이용자 정보 조회(지역명,인구수)")
    @PostMapping("/getallmember")
    private BaseResponseDto findPopulation(HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("Authorization");
        tokenValidator.checkTokenIsNull(token);
        Member findMember = memberService.findMemberByJwtToken(token);
        return memberService.findPopulation();
    }

    @Operation(summary = "앱 이용자 업데이트", description = "앱 이용자 정보 업데이트(인구수)")
    @PutMapping("/getallmember")
    private BaseResponseDto updatePopulation(@RequestBody MapPopulationRequestDto mapPopulationRequestDto) {
        return memberService.updatePopulation(mapPopulationRequestDto);
    }

}
