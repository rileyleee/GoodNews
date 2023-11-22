package com.goodnews.map.exception.validator;


import com.goodnews.map.exception.CustomException;
import com.goodnews.map.exception.message.MapErrorEnum;
import com.goodnews.map.map.domain.OffMapInfo;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Component
public class MapValidator {



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
