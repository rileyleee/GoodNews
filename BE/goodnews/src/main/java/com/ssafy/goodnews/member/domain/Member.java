package com.ssafy.goodnews.member.domain;

import com.ssafy.goodnews.common.domain.BaseConnectEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Entity
@NoArgsConstructor
@ToString
public class Member extends BaseConnectEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String name;
    private String birthDate;
    private String gender;
    private String bloodType;
    private String addInfo;
    private String state;
    private Double lat;
    private Double lon;


}
