package com.goodnews.member.common.exception.validator;

import com.goodnews.member.common.exception.CustomException;
import com.goodnews.member.common.exception.message.MapErrorEnum;
import com.goodnews.member.map.domain.LocalPopulation;
import com.goodnews.member.map.domain.OffMapInfo;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

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



}
