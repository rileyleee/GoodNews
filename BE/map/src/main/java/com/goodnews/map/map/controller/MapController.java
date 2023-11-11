package com.goodnews.map.map.controller;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.goodnews.map.map.dto.request.MapFacilityRequestDto;
import com.goodnews.map.map.dto.request.MapPopulationRequestDto;
import com.goodnews.map.map.dto.request.MapRegistFacilityRequestDto;
import com.goodnews.map.map.service.MapService;
import com.goodnews.map.util.BaseResponseDto;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "시설 정보 전체 조회", description = "시설 전체 정보(타입,명칭,경도,위도,시설자원) 조회")
    @GetMapping("/facilityinfo")
    private BaseResponseDto findFacilityInfo(
                                             @RequestParam(
                                                     required = false,
                                                     defaultValue = "1",
                                                     value = "page") int page,
                                             @RequestParam(
                                                     required = false,
                                                     defaultValue = "10",
                                                     value = "size") int size) {
        return mapService.findFacilityInfo(page - 1, size);
    }


    @Operation(summary = "지도 시설 상세 보기", description = "지도시설 상세 보기(타입,명칭,경도,위도,사용가능,보유자원)조회")
    @PostMapping("/facilitydetail")
    private BaseResponseDto findDetailFacilityInfo(@RequestBody MapFacilityRequestDto mapFacilityRequestDto) {
        return mapService.detailFacility(mapFacilityRequestDto.getId());
    }

}
