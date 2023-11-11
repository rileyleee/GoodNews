package com.goodnews.member.map.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.goodnews.member.map.dto.request.MapFacilityRequestDto;
import com.goodnews.member.map.dto.request.MapPopulationRequestDto;
import com.goodnews.member.map.dto.request.MapRegistFacilityRequestDto;
import com.goodnews.member.common.dto.BaseResponseDto;
import com.goodnews.member.common.exception.validator.TokenValidator;
import com.goodnews.member.map.service.MapService;
import com.goodnews.member.member.domain.Member;
import com.goodnews.member.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Tag(name = "Map", description = "지도 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/m")
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
    private BaseResponseDto updatePopulation(@RequestBody MapPopulationRequestDto mapPopulationRequestDto) {
        return mapService.updatePopulation(mapPopulationRequestDto);
    }

    @Operation(summary = "지도 시설 상세 보기", description = "지도시설 상세 보기(타입,명칭,경도,위도,사용가능,보유자원)조회")
    @PostMapping("/facilitydetail")
    private BaseResponseDto findDetailFacilityInfo(HttpServletRequest httpServletRequest, @RequestBody MapFacilityRequestDto mapFacilityRequestDto) {
        String token = httpServletRequest.getHeader("Authorization");
        tokenValidator.checkTokenIsNull(token);
        Member findMember = memberService.findMemberByJwtToken(token);
        return mapService.detailFacility(mapFacilityRequestDto.getId());
    }

    @Operation(summary = "지도 시설 상태 등록", description = "지도시설(버튼타입,내용,경도,위도)등록")
    @PostMapping("/registfacility")
    private BaseResponseDto registMapFacility(@RequestBody MapRegistFacilityRequestDto mapRegistFacilityRequestDto) throws JsonProcessingException {

        return mapService.registFacility(mapRegistFacilityRequestDto);
    }

    @Operation(summary = "지도 시설 상태 조회", description = "지도시설(버튼타입,내용,경도,위도)조회")
    @PostMapping("/getfacilitystate")
    private BaseResponseDto getMapFacility() {

        return mapService.getFacilityState();
    }
}
