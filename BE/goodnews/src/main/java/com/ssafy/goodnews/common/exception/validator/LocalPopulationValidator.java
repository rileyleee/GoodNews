package com.ssafy.goodnews.common.exception.validator;

import com.ssafy.goodnews.common.exception.CustomException;
import com.ssafy.goodnews.common.exception.message.MemberErrorEnum;
import com.ssafy.goodnews.common.exception.message.PopulationErrorEnum;
import com.ssafy.goodnews.map.domain.LocalPopulation;
import com.ssafy.goodnews.member.domain.Member;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Component
public class LocalPopulationValidator {

    public void checkLocalPopulation(Optional<LocalPopulation> population, int id) {
        if (population.isEmpty()) {
            throw CustomException.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .code(PopulationErrorEnum.INVALID_Population.getCode())
                    .message(PopulationErrorEnum.INVALID_Population.getMessage() + id)
                    .build();
        }
    }



}
