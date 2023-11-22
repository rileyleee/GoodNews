package com.goodnews.member.common.exception.message;

import lombok.Getter;

@Getter
public enum MemberErrorEnum {
    INVALID_MEMBER(2000, "잘못된 사용자입니다. memberId: "),
    INVALID_ADMIN(2001,"관리자가 아닙니다. memberId: "),
    INVALID_PHONE(2002,"해당 전화번호의 사용자가 없습니다. phoneNumber: ");

    private final int code;
    private final String message;

    MemberErrorEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
