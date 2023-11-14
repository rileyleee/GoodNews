package com.ssafy.goodnews.member.dto.response.family;

import lombok.Builder;
import lombok.Getter;

@Getter
public class FamilyPlaceInfoResponseDto {

    private int placeId;
    private String name;
    private boolean canuse;
    private int seq;

    @Builder
    public FamilyPlaceInfoResponseDto(int placeId, String name, boolean canuse,int seq) {
        this.placeId = placeId;
        this.name = name;
        this.canuse = canuse;
        this.seq = seq;
    }
}
