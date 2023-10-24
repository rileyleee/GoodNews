package com.ssafy.goodnews.member.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberFirstLoginResponseDto {

    private boolean firstLogin;

    @Builder
    public MemberFirstLoginResponseDto(boolean firstLogin) {
        this.firstLogin = firstLogin;
    }
}
