package com.ssafy.goodnews.map.controller;

import com.ssafy.goodnews.common.dto.BaseResponseDto;
import com.ssafy.goodnews.common.exception.validator.TokenValidator;
import com.ssafy.goodnews.jwt.JwtTokenProvider;
import com.ssafy.goodnews.map.dto.request.MapPopulationRequestDto;
import com.ssafy.goodnews.map.service.MapService;
import com.ssafy.goodnews.member.domain.Member;
import com.ssafy.goodnews.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Tag(name = "Map", description = "지도 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/map")
public class MapController {

    private final MapService mapService;
    private final MemberService memberService;
    private final TokenValidator tokenValidator;
    @Operation(summary = "시설 정보 전체 조회", description = "시설 전체 정보(타입,명칭,경도,위도,시설자원) 조회")
    @GetMapping("/facilityinfo")
    private BaseResponseDto findFacilityInfo(HttpServletRequest httpServletRequest,
                                             @RequestParam(
                                                     required = false,
                                                     defaultValue = "1",
                                                     value = "page") int page,
                                             @RequestParam(
                                                     required = false,
                                                     defaultValue = "10",
                                                     value = "size") int size) {
        String token = httpServletRequest.getHeader("Authorization");
        tokenValidator.checkTokenIsNull(token);
        Member findMember = memberService.findMemberByJwtToken(token);

        return mapService.findFacilityInfo(page - 1, size);
    }

    @Operation(summary = "앱 이용자 조회", description = "앱 이용자 정보 조회(지역명,인구수)")
    @PostMapping("/getallmember")
    private BaseResponseDto findPopulation(HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("Authorization");
        tokenValidator.checkTokenIsNull(token);
        Member findMember = memberService.findMemberByJwtToken(token);
        return mapService.findPopulation();
    }

    @Operation(summary = "앱 이용자 업데이트", description = "앱 이용자 정보 업데이트(인구수)")
    @PutMapping("/getallmember")
    private BaseResponseDto updatePopulation(HttpServletRequest httpServletRequest, @RequestBody MapPopulationRequestDto mapPopulationRequestDto) {
        String token = httpServletRequest.getHeader("Authorization");
        tokenValidator.checkTokenIsNull(token);
        Member findMember = memberService.findMemberByJwtToken(token);
        return mapService.updatePopulation(mapPopulationRequestDto);
    }

}
