package com.goodnews.member.member.dto.request.member;

import lombok.Getter;

@Getter
public class MemberRegistRequestDto {

    private String memberId;
    private String phoneNumber;
    private String name;
    private String birthDate;
    private String gender;
    private String bloodType;
    private String addInfo;

}
