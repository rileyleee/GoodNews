package com.goodnews.member.member.dto.request.member;

import lombok.Getter;

@Getter
public class MemberInfoUpdateRequestDto {

    private String name;
    private String birthDate;
    private String bloodType;
    private String addInfo;
}
