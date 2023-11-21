package com.ssafy.goodnews.common.exception.validator;

import com.ssafy.goodnews.common.exception.CustomException;
import com.ssafy.goodnews.common.exception.message.MapErrorEnum;
import com.ssafy.goodnews.map.domain.LocalPopulation;
import com.ssafy.goodnews.map.domain.OffMapInfo;
import com.ssafy.goodnews.map.dto.response.FacilityStateResponseDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Component
public class MapValidator {

    public void checkLocalPopulation(Optional<LocalPopulation> population, int id) {
        if (population.isEmpty()) {
            throw CustomException.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .code(MapErrorEnum.INVALID_POPULATION.getCode())
                    .message(MapErrorEnum.INVALID_POPULATION.getMessage() + id)
                    .build();
        }
    }

    public void checkFaciltiy(Optional<OffMapInfo> offMapInfo, String id) {
        if (offMapInfo.isEmpty()) {
            throw CustomException.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .code(MapErrorEnum.INVALID_FACILITY.getCode())
                    .message(MapErrorEnum.INVALID_FACILITY.getMessage() + id)
                    .build();
        }
    }

    public void checkFaciltiyState(List<FacilityStateResponseDto> facilityStateResponseDtos,String date) {
        if (facilityStateResponseDtos.isEmpty()) {
            throw CustomException.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .code(MapErrorEnum.INVALID_FACILITY_STATE.getCode())
                    .message(date+ MapErrorEnum.INVALID_FACILITY_STATE.getMessage())
                    .build();
        }
    }



}
