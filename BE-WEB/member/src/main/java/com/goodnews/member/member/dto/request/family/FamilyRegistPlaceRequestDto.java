package com.goodnews.member.member.dto.request.family;

import lombok.Getter;

@Getter
public class FamilyRegistPlaceRequestDto {

    private String memberId;
    private String name;
    private Double lat;
    private Double lon;
    private int seq;
    private String address;

}
