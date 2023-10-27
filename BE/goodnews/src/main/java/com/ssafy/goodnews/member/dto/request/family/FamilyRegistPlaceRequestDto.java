package com.ssafy.goodnews.member.dto.request.family;

import lombok.Getter;

@Getter
public class FamilyRegistPlaceRequestDto {

    private String memberId;
    private String name;
    private Double lat;
    private Double lon;
}
