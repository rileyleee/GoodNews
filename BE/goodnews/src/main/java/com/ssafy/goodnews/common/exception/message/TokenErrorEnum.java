package com.ssafy.goodnews.common.exception.message;

import lombok.Getter;

@Getter
public enum TokenErrorEnum {
    INVALID_REFRESHTOKEN(1000,"refreshtoken이 일치하지 않습니다!!"),
    INVALID_TOKEN(1001,"토큰이 NULL입니다");
    private final int code;
    private final String message;

    TokenErrorEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
