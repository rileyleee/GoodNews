package com.ssafy.goodnews.map.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.goodnews.common.dto.BaseResponseDto;
import com.ssafy.goodnews.common.exception.validator.BaseValidator;
import com.ssafy.goodnews.common.exception.validator.MapValidator;
import com.ssafy.goodnews.map.domain.FacilityState;
import com.ssafy.goodnews.map.domain.LocalPopulation;
import com.ssafy.goodnews.map.domain.OffMapInfo;
import com.ssafy.goodnews.map.dto.request.MapPopulationRequestDto;
import com.ssafy.goodnews.map.dto.request.MapRegistFacilityRequestDto;
import com.ssafy.goodnews.map.dto.response.FacilityStateResponseDto;
import com.ssafy.goodnews.map.dto.response.MapPopulationResponseDto;
import com.ssafy.goodnews.map.dto.response.MapResponseDto;
import com.ssafy.goodnews.map.repository.FacilityStateRepository;
import com.ssafy.goodnews.map.repository.LocalPopulationRepository;
import com.ssafy.goodnews.map.repository.MapMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final LocalPopulationRepository localPopulationRepository;
    private final MapValidator mapValidator;
    private final RedisTemplate<String,String> redisTemplate;
    private final FacilityStateRepository facilityStateRepository;


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
            mapValidator.checkLocalPopulation(findLocalPopulation, localPopulationDto.getId());
            findLocalPopulation.ifPresent(lp -> lp.updatePopulation(localPopulationDto));
        });


        return BaseResponseDto.builder()
                .success(true)
                .message("앱 사용자 인구 수 업데이트 성공했습니다")
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

    @Transactional
    public BaseResponseDto registFacility(MapRegistFacilityRequestDto request) throws JsonProcessingException {

        saveToRedis(request);

        return BaseResponseDto.builder()
                .success(true)
                .message("시설 상태 정보를 등록했습니다")
                .build();
    }


    public void saveToRedis(MapRegistFacilityRequestDto request) throws JsonProcessingException {
        // Redis에 데이터 저장
        String key = request.getLat() + ":" + request.getLon();
        redisTemplate.opsForValue().set(key, new ObjectMapper().writeValueAsString(request), 1, TimeUnit.HOURS);
    }
    @Transactional
    @Scheduled(fixedRate = 600000) // 1분마다 실행 (원하는 시간으로 조정 가능)
    public void saveToDatabaseFromRedis() throws JsonProcessingException {
        // Redis에서 모든 키 가져오기
        Set<String> allKeys = redisTemplate.keys("*");
        Set<String> filteredKeys = allKeys.stream()
                .filter(key -> !key.contains("admin"))
                .collect(Collectors.toSet());
        for (String key : filteredKeys) {
            String value = redisTemplate.opsForValue().get(key);
            MapRegistFacilityRequestDto request = new ObjectMapper().readValue(value, MapRegistFacilityRequestDto.class);
            // DB에 동일한 lat, lon이 있는지 확인
            Optional<FacilityState> findFacilityState = facilityStateRepository.findByLatAndLon(request.getLat(), request.getLon());
            if (findFacilityState.isPresent()) {
                // 업데이트 로직
                findFacilityState.get().updateState(request);

            } else {
                // 삽입 로직
                facilityStateRepository.save(FacilityState.builder()
                        .mapRegistFacilityRequestDto(request)
                        .build());
            }
        }
    }


    @Transactional(readOnly = true)
    public BaseResponseDto getFacility() {

        return BaseResponseDto.builder()
                .success(true)
                .message("시설 상태 정보 전체 조회 성공했습니다")
                .data(facilityStateRepository.findAll())
                .build();
    }

    @Transactional(readOnly = true)
    public BaseResponseDto getDurationFacility(String date) {
        List<FacilityStateResponseDto> list = facilityStateRepository.findByLastModifiedDateAfter(date).stream()
                .map(facilityState -> FacilityStateResponseDto.builder()
                        .id(facilityState.getId())
                        .buttonType(facilityState.getButtonType())
                        .text(facilityState.getText())
                        .lon(facilityState.getLon())
                        .lat(facilityState.getLat())
                        .lastModifiedDate(facilityState.getLastModifiedDate())
                        .build())
                .collect(Collectors.toList());
        mapValidator.checkFaciltiyState(list,date);
        return BaseResponseDto.builder()
                .success(true)
                .message("기간 이후 시설 상태 정보 조회를 성공했습니다")
                .data(list)
                .build();
    }
}
