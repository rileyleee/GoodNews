package com.goodnews.member.member.dto.response.member;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberResponseDto {


    private String memberId;
    private String phoneNumber;
    private String name;
    private String lastConnection;
    private String state;
    private int familyId;

    @Builder
    public MemberResponseDto(String memberId,String phoneNumber, String name, String lastConnection,String state, int familyId) {
        this.memberId = memberId;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.lastConnection = lastConnection;
        this.state = state;
        this.familyId = familyId;
    }
}
