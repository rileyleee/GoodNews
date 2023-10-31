package com.ssafy.goodnews.common.exception.message;

import lombok.Getter;

@Getter
public enum PopulationErrorEnum {

    INVALID_Population(4000, "잘못된 지역입니다. id: ");

    private final int code;
    private final String message;

    PopulationErrorEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
