package com.goodnews.member.member.dto.response.member;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberRegistFamilyResposneDto {

    private int familyId;

    @Builder
    public MemberRegistFamilyResposneDto(int familyId) {
        this.familyId = familyId;
    }
}
