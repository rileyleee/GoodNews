package com.goodnews.member.member.dto.response.family;

import lombok.Builder;
import lombok.Getter;

@Getter
public class FamilyInviteResponseDto {

    private int id;
    private String name;
    private String phoneNumber;


    @Builder
    public FamilyInviteResponseDto(int id, String name, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }
}
