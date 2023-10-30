package com.ssafy.goodnews.common.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class RefreshTokenResponseDto {
    private String memberId;
    private String message;

    @Builder
    public RefreshTokenResponseDto(String memberId, String message) {
        this.memberId = memberId;
        this.message = message;
    }
}
