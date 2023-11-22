package com.goodnews.member.common.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginDto {

    private String memberId;


    @Builder
    public LoginDto(String memberId) {
        this.memberId = memberId;
    }
}
