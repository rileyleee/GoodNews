package com.ssafy.goodnews.member.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberResponseDto {


    private String memberId;
    private String name;
    private String lastConnection;

    @Builder
    public MemberResponseDto(String memberId, String name, String lastConnection) {
        this.memberId = memberId;
        this.name = name;
        this.lastConnection = lastConnection;
    }
}
