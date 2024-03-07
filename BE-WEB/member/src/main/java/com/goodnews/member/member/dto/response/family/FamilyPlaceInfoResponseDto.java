package com.goodnews.member.member.dto.response.family;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class FamilyPlaceInfoResponseDto {

    private int placeId;
    private String name;
    private boolean canuse;
    private int seq;
    private String createdDate;

    @Builder
    public FamilyPlaceInfoResponseDto(int placeId, String name, boolean canuse, int seq, String createdDate) {
        this.placeId = placeId;
        this.name = name;
        this.canuse = canuse;
        this.seq = seq;
        this.createdDate = createdDate;
    }
}
