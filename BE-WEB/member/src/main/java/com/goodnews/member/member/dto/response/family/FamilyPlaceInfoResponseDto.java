package com.goodnews.member.member.dto.response.family;

import lombok.Builder;
import lombok.Getter;

@Getter
public class FamilyPlaceInfoResponseDto {

    private int placeId;
    private String name;
    private boolean canuse;
    private int seq;
    private String address;

    @Builder
    public FamilyPlaceInfoResponseDto(int placeId, String name, boolean canuse,int seq,String address) {
        this.placeId = placeId;
        this.name = name;
        this.canuse = canuse;
        this.seq = seq;
        this.address = address;
    }
}
