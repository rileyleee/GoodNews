package com.ssafy.goodnews.member.dto.response;

import com.ssafy.goodnews.member.domain.FamilyPlace;
import lombok.Builder;
import lombok.Getter;

@Getter
public class FamilyRegistPlaceResponseDto {

    private int placeId;
    private String name;
    private Double lat;
    private Double lon;
    private boolean canuse;

    @Builder
    public FamilyRegistPlaceResponseDto(FamilyPlace familyPlace) {
        this.placeId = familyPlace.getId();
        this.name = familyPlace.getName();
        this.lat = familyPlace.getLat();
        this.lon = familyPlace.getLon();
        this.canuse = familyPlace.isCanuse();
    }
}
