package com.ssafy.goodnews.map.service;

import com.ssafy.goodnews.common.dto.BaseResponseDto;
import com.ssafy.goodnews.common.exception.validator.BaseValidator;
import com.ssafy.goodnews.common.exception.validator.LocalPopulationValidator;
import com.ssafy.goodnews.map.domain.LocalPopulation;
import com.ssafy.goodnews.map.domain.OffMapInfo;
import com.ssafy.goodnews.map.dto.request.LocalPopulationDto;
import com.ssafy.goodnews.map.dto.request.MapPopulationRequestDto;
import com.ssafy.goodnews.map.dto.response.MapPopulationResponseDto;
import com.ssafy.goodnews.map.repository.LocalPopulationRepository;
import com.ssafy.goodnews.map.repository.MapMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MapService {

    private final MapMongoRepository mongoRepository;
    private final BaseValidator baseValidator;
    private final LocalPopulationRepository localPopulationRepository;
    private final LocalPopulationValidator localPopulationValidator;



    @Transactional(readOnly = true)
    public BaseResponseDto test() {

        Optional<OffMapInfo> findId = mongoRepository.findFirstByNameRegex("뉴코아아울렛");
        return BaseResponseDto.builder()
                .data(findId.get())
                .build();
    }

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
    public BaseResponseDto findPopulation() {

        return BaseResponseDto.builder()
                .success(true)
                .message("앱 이용자 조회를 성공했습니다")
                .data( localPopulationRepository.findAll().stream()
                        .map(localPopulation -> MapPopulationResponseDto.builder()
                                .id(localPopulation.getId())
                                .name(localPopulation.getName())
                                .population(localPopulation.getPopulation())
                                .build()))
                .build();
    }

    @Transactional
    public BaseResponseDto updatePopulation(MapPopulationRequestDto mapPopulationRequestDto) {


        mapPopulationRequestDto.getPopulationList().forEach(localPopulationDto -> {
            Optional<LocalPopulation> findLocalPopulation = localPopulationRepository.findById(localPopulationDto.getId());
            localPopulationValidator.checkLocalPopulation(findLocalPopulation, localPopulationDto.getId());
            findLocalPopulation.ifPresent(lp -> lp.updatePopulation(localPopulationDto));
        });


        return BaseResponseDto.builder()
                .success(true)
                .message("앱 사용자 인구 수 업데이트 성공했습니다")
                .build();
    }


//    private List<MapResponseDto> getMapInfoList(OffMapInfo offMapInfo) {
//        return offMapInfo
//    }
}
