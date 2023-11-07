package com.goodnews.member.member.dto.response.family;

import lombok.Builder;
import lombok.Getter;

@Getter
public class FamilyPlaceInfoResponseDto {

    private int placeId;
    private String name;
    private boolean canuse;

    @Builder
    public FamilyPlaceInfoResponseDto(int placeId, String name, boolean canuse) {
        this.placeId = placeId;
        this.name = name;
        this.canuse = canuse;
    }
}
