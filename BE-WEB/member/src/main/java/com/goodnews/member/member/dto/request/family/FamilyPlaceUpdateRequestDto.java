package com.goodnews.member.member.dto.request.family;

import lombok.Getter;

@Getter
public class FamilyPlaceUpdateRequestDto {

    private String registerUser;
    private String name;
    private Double lat;
    private Double lon;
}
