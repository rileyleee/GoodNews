package com.goodnews.map.map.service;

import com.goodnews.map.exception.validator.BaseValidator;
import com.goodnews.map.exception.validator.MapValidator;
import com.goodnews.map.map.domain.OffMapInfo;
import com.goodnews.map.map.dto.request.MapPopulationRequestDto;
import com.goodnews.map.map.dto.request.MapRegistFacilityRequestDto;
import com.goodnews.map.map.dto.response.MapPopulationResponseDto;
import com.goodnews.map.map.dto.response.MapResponseDto;
import com.goodnews.map.map.repository.MapMongoRepository;
import com.goodnews.map.util.BaseResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MapService {

    private final MapMongoRepository mongoRepository;
    private final BaseValidator baseValidator;
    private final MapValidator mapValidator;





    @Transactional(readOnly = true)
    public BaseResponseDto findFacilityInfo(int page, int size) {
        baseValidator.checkPageAndSize(page,size);
        PageRequest pageable = PageRequest.of(page, size);

        return BaseResponseDto.builder()
                .success(true)
                .message("전체 시설 정보 조회를 성공했습니다")
                .data(mongoRepository.findAll())
                .build();
    }


    @Transactional(readOnly = true)
    public BaseResponseDto detailFacility(String id) {

        Optional<OffMapInfo> findFaciltiy = mongoRepository.findById(id);
        mapValidator.checkFaciltiy(findFaciltiy,id);
        return BaseResponseDto.builder()
                .success(true)
                .message("지도 상세 정보를 조회했습니다")
                .data(MapResponseDto.builder()
                        .type(findFaciltiy.get().getType())
                        .name(findFaciltiy.get().getName())
                        .lon(findFaciltiy.get().getLon())
                        .lat(findFaciltiy.get().getLat())
                        .canuse(findFaciltiy.get().getCanuse())
                        .facility(findFaciltiy.get().getFacility())
                        .build())
                .build();
    }


}
