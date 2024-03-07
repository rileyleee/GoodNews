package com.goodnews.member.member.dto.response.member;

import com.goodnews.member.member.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberInfoResponseDto {

    private String memberId;
    private String phoneNumber;
    private String name;
    private String birthDate;
    private String gender;
    private String bloodType;
    private String addInfo;
    private String state;
    private Double lat;
    private Double lon;
    private int familyId;

    @Builder
    public MemberInfoResponseDto(Member member, int familyId) {
        this.memberId = member.getId();
        this.phoneNumber = member.getPhoneNumber();
        this.name = member.getName();
        this.birthDate = member.getBirthdate();
        this.gender = member.getGender();
        this.bloodType = member.getBloodtype();
        this.addInfo = member.getAddinfo();
        this.state = member.getState();
        this.lat = member.getLat();
        this.lon = member.getLon();
        this.familyId = familyId;
    }
}
