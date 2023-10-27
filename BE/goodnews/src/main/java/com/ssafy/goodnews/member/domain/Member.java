package com.ssafy.goodnews.member.domain;

import com.ssafy.goodnews.common.domain.BaseConnectEntity;
import com.ssafy.goodnews.member.dto.request.member.MemberInfoUpdateRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@ToString
public class Member extends BaseConnectEntity {
    @Id
    private String id;
    private String name;
    private String birthdate;
    private String gender;
    private String bloodtype;
    private String addinfo;
    private String state;
    private Double lat;
    private Double lon;

    private LocalDateTime lastConnection;

    public LocalDateTime getLastConnection() {
        return lastConnection;
    }

    public void setLastConnection(LocalDateTime lastConnection) {
        this.lastConnection = lastConnection;
    }

    @Builder
    public Member(String id, String name, String birthDate, String gender, String bloodType, String addInfo,LocalDateTime lastConnection) {
        this.id = id;
        this.name = name;
        this.birthdate = birthDate;
        this.gender = gender;
        this.bloodtype = bloodType;
        this.addinfo = addInfo;
        this.lastConnection = LocalDateTime.now();
    }

    public void updateMemberInfo(MemberInfoUpdateRequestDto memberInfoUpdateRequestDto) {
        this.name = memberInfoUpdateRequestDto.getName();
        this.birthdate = memberInfoUpdateRequestDto.getBirthDate();
        this.bloodtype= memberInfoUpdateRequestDto.getBloodType();
        this.addinfo = memberInfoUpdateRequestDto.getAddInfo();
    }
}
