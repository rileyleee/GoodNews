package com.ssafy.goodnews.member.dto.request.family;

import lombok.Getter;

@Getter
public class FamilyPlaceUpdateRequestDto {

    private String name;
    private Double lat;
    private Double lon;
}
