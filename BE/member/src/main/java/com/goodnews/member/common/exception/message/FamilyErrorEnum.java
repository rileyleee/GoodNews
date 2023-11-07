package com.goodnews.member.common.exception.message;

import lombok.Getter;

@Getter
public enum FamilyErrorEnum {
    INVALID_FAMILY_MEMBER(3002,"가족 신청을 하지 않은 사용자입니다 . memberId: "),
    INVALID_FAMILY(3000, "이미 가족신청을 요청하셨습니다. memberId: "),
    INVALID_FAMILY_OTHER(3001,"이미 가족요청을 받았습니다: memberId: "),
    INVALID_FAMILY_PLACE(3003,"가족 모임 장소가 없습니다");
    private final int code;
    private final String message;

    FamilyErrorEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
