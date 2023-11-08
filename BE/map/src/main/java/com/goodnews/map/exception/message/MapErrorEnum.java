package com.goodnews.map.exception.message;

import lombok.Getter;

@Getter
public enum MapErrorEnum {

    INVALID_POPULATION(4000, "잘못된 지역입니다. id: "),
    INVALID_FACILITY(4001,"잘못된 지도 정보입니다. id:");
    private final int code;
    private final String message;

    MapErrorEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
