package com.goodnews.member.member.controller.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.goodnews.member.member.dto.request.facility.FacilityDurationReqeustDto;
import com.goodnews.member.member.dto.request.facility.MapRegistFacilityRequestDto;
import com.goodnews.member.common.dto.BaseResponseDto;
import com.goodnews.member.common.exception.validator.TokenValidator;
import com.goodnews.member.member.service.FacilityService;
import com.goodnews.member.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Facility", description = "시설 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/facility/app")
public class FacilityController {

    private final FacilityService mapService;





    @Operation(summary = "시설 상태 등록", description = "시설(버튼타입,내용,경도,위도,수정시간(yyyy-mm-dd hh:mm:ss)등록")
    @PostMapping("/registfacility")
    private BaseResponseDto registMapFacility(@RequestBody MapRegistFacilityRequestDto mapRegistFacilityRequestDto) throws JsonProcessingException {

        return mapService.registFacility(mapRegistFacilityRequestDto);
    }

    @Operation(summary = "시설 상태 조회", description = "시설(버튼타입,내용,경도,위도)등록")
    @PostMapping("/getfacility")
    private BaseResponseDto getMapFacility() {

        return mapService.getFacility();
    }

    @Operation(summary = "시설 상태 기간 이후 조회", description = "기간(yyyy-mm-dd hh:mm:ss)요청하면(버튼타입,내용,경도,위도)조회")
    @PostMapping("/afterfacility")
    private BaseResponseDto getDurationFacility(@RequestBody FacilityDurationReqeustDto facilityDurationReqeustDto) {

        return mapService.getDurationFacility(facilityDurationReqeustDto.getDate());
    }
}
