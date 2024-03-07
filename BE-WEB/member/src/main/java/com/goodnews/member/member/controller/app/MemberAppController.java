package com.goodnews.member.member.controller.app;

import com.goodnews.member.common.dto.BaseResponseDto;
import com.goodnews.member.common.dto.LoginDto;
import com.goodnews.member.member.dto.request.member.*;
import com.goodnews.member.jwt.JwtTokenProvider;
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
@RequestMapping("/api/members/app")
public class MemberAppController {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberService memberService;

    @Operation(summary = "추가 정보 등록(회원 가입)", description = "멤버 추가 정보(기기번호,전화번호,이름,생년월일,성별,혈액형,특이사항) 등록")
    @PostMapping("/registinfo")
    private BaseResponseDto registMemberInfo(@RequestBody MemberRegistRequestDto memberRegistRequestDto) {


        return memberService.registMemberInfo(memberRegistRequestDto);
    }

    @Operation(summary = "최초 로그인 유무 조회", description = "최초로그인 조회")
    @PostMapping("/firstlogin")
    private BaseResponseDto firstLoginInfo(@RequestBody MemberFirstLoginRequestDto memberFirstLoginRequestDto) {


        return memberService.firstLoginSearch(memberFirstLoginRequestDto);
    }
    @Operation(summary = "멤버 정보 수정", description = "멤버 정보(이름,생년월일,혈액형,특이사항,성별,경도,위도) 수정")
    @PutMapping("/{memberId}")
    private BaseResponseDto updateMemberInfo(@PathVariable String memberId, @RequestBody MemberInfoUpdateRequestDto memberInfoUpdateRequestDto) {


        return memberService.updateMemberInfo(memberId,memberInfoUpdateRequestDto);
    }

    @Operation(summary = "멤버 정보 조회", description = "멤버 정보(전화번호,이름,생년월일,성별,혈액형,특이사항,상태(0:모름,1:건강,2:부싱,3:죽음),familyId 조회")
    @PostMapping("/search")
    private BaseResponseDto findMemberInfo(@RequestBody MemberFirstLoginRequestDto memberFirstLoginRequestDto) {

        return memberService.getMemberInfo(memberFirstLoginRequestDto.getMemberId());
    }

    @Operation(summary = "멤버 상태 정보 수정", description = "멤버 상태(0:모름,1:건강,2:부싱,3:죽음) 수정")
    @PutMapping("/state/{memberId}")
    private BaseResponseDto updateMemberState(@PathVariable String memberId, @RequestBody MemberStateRequestDto memberStateRequestDto) {


        return memberService.updateMemberState(memberId,memberStateRequestDto.getState());
    }


    @Operation(summary = "멤버 위치 및 연결시각 업데이트", description = "멤버 위도,경도,마지막 연결시각 업데이트 ")
    @PutMapping("/get/{memberId}")
    private BaseResponseDto updateMember(@PathVariable String memberId, @RequestBody MemberUpdateDto memberUpdateDto) {


        return memberService.updateMember(memberId,memberUpdateDto);
    }

    @Operation(summary = "전화번호 입력시 해당 회원 이름 조회", description = "멤버 이름 조회 ")
    @PostMapping("/get/phone")
    private BaseResponseDto getPhone(@RequestBody MemberPhoneRequestDto memberPhoneRequestDto) {
        return memberService.getPhoneNumber(memberPhoneRequestDto.getPhoneNumber());
    }

    @Operation(summary = "회원가입 탈퇴", description = "회원 탈퇴")
    @DeleteMapping("/deleteUser")
    private BaseResponseDto deleteUser(@RequestBody MemberFirstLoginRequestDto memberFirstLoginRequestDto){
        return memberService.deleteUser(memberFirstLoginRequestDto);
    }
}
