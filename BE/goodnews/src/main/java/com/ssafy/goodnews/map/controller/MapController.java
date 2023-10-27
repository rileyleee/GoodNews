package com.ssafy.goodnews.map.controller;

import com.ssafy.goodnews.common.dto.BaseResponseDto;
import com.ssafy.goodnews.map.service.MapService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Map", description = "지도 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/map")
public class MapController {


    private final MapService mapService;


    @Operation(summary = "멤버 정보 조회", description = "멤버 정보(전화번호,이름,생년월일,성별,혈액형,특이사항) 조회")
    @GetMapping("/search")
    private BaseResponseDto findMemberInfo() {


        return mapService.test();
    }
}
