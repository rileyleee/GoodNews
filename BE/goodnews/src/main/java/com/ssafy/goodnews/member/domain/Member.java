package com.ssafy.goodnews.member.domain;

import com.ssafy.goodnews.common.domain.BaseConnectEntity;
import com.ssafy.goodnews.member.dto.request.member.MemberInfoUpdateRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@ToString
public class Member extends BaseConnectEntity {
    @Id
    private String id;
    private String phoneNumber;
    private String name;
    private String birthdate;
    private String gender;
    private String bloodtype;
    private String addinfo;
    private int state;
    private Double lat;
    private Double lon;
    private String password;


    @Enumerated(EnumType.STRING)
    private Role role;


    private LocalDateTime lastConnection;

    public LocalDateTime getLastConnection() {
        return lastConnection;
    }


    public void updateAllMemberInfo(Member member) {
        this.id = member.getId();
        this.phoneNumber = member.getPhoneNumber();
        this.name = member.getName();
        this.birthdate = member.getBirthdate();
        this.gender = member.getGender();
        this.bloodtype = member.getBloodtype();
        this.addinfo = member.getAddinfo();
        this.lat = member.getLat();
        this.lon = member.getLon();
    }

    public void setLastConnection(LocalDateTime lastConnection) {
        this.lastConnection = lastConnection;
    }

    @Builder
    public Member(String id,String phoneNumber, String name, String birthDate, String gender, String bloodType, String addInfo,LocalDateTime lastConnection,Role role,int state) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.birthdate = birthDate;
        this.gender = gender;
        this.bloodtype = bloodType;
        this.addinfo = addInfo;
        this.lastConnection = LocalDateTime.now();
        this.role = Role.USER;
        this.state = state;
    }


    public void updateMemberInfo(MemberInfoUpdateRequestDto memberInfoUpdateRequestDto) {
        this.name = memberInfoUpdateRequestDto.getName();
        this.birthdate = memberInfoUpdateRequestDto.getBirthDate();
        this.bloodtype= memberInfoUpdateRequestDto.getBloodType();
        this.addinfo = memberInfoUpdateRequestDto.getAddInfo();
    }



    public void updateMemberState(int state) {
        this.state = state;
    }
}
