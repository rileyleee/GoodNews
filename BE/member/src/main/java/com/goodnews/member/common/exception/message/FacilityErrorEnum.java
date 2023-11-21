package com.goodnews.member.common.exception.message;

import lombok.Getter;

@Getter
public enum FacilityErrorEnum {

    INVALID_POPULATION(4000, "잘못된 지역입니다. id: "),
    INVALID_FACILITY(4001,"잘못된 지도 정보입니다. id:"),
    INVALID_FACILITY_STATE(4003," 기간 이후로 조회되는 정보가 없습니다");
    private final int code;
    private final String message;

    FacilityErrorEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
