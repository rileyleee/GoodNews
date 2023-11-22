package com.goodnews.member.common.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TokenDto {

    private String accessToken;
    private String refreshToken;
    private String memberId;

    @Builder
    public TokenDto(String accessToken, String refreshToken, String memberId) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.memberId = memberId;
    }
}
