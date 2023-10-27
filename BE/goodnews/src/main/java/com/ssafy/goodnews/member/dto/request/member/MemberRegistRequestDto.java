package com.ssafy.goodnews.member.dto.request.member;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberRegistRequestDto {

    private String memberId;
    private String name;
    private String birthDate;
    private String gender;
    private String bloodType;
    private String addInfo;

}
