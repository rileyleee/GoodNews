package com.goodnews.member.common.exception.validator;

import com.goodnews.member.common.exception.CustomException;
import com.goodnews.member.common.exception.message.FacilityErrorEnum;
import com.goodnews.member.member.domain.LocalPopulation;
import com.goodnews.member.member.dto.response.facility.FacilityStateResponseDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Component
public class FacilityValidator {

    public void checkLocalPopulation(Optional<LocalPopulation> population, int id) {
        if (population.isEmpty()) {
            throw CustomException.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .code(FacilityErrorEnum.INVALID_POPULATION.getCode())
                    .message(FacilityErrorEnum.INVALID_POPULATION.getMessage() + id)
                    .build();
        }
    }


    public void checkFaciltiyState(List<FacilityStateResponseDto> facilityStateResponseDtos, String date) {
        if (facilityStateResponseDtos.isEmpty()) {
            throw CustomException.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .code(FacilityErrorEnum.INVALID_FACILITY_STATE.getCode())
                    .message(date+ FacilityErrorEnum.INVALID_FACILITY_STATE.getMessage())
                    .build();
        }
    }




}
