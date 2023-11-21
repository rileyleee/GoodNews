package com.ssafy.goodnews.member.dto.response.member;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberResponseDto {


    private String memberId;
    private String phoneNumber;
    private String name;
    private String lastConnection;
    private String state;
    private String familyId;

    @Builder
    public MemberResponseDto(String memberId,String phoneNumber, String name, String lastConnection,String state,String familyId) {
        this.memberId = memberId;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.lastConnection = lastConnection;
        this.state = state;
        this.familyId = familyId;
    }
}
