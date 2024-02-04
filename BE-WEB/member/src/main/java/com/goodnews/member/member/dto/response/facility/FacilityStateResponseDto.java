package com.goodnews.member.member.dto.response.facility;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class FacilityStateResponseDto {

    private String id;
    private Boolean buttonType;
    private String text;

    private Double lat;
    private Double lon;

    private LocalDateTime createdDate;

    @Builder
    public FacilityStateResponseDto(String id, Boolean buttonType, String text, Double lat, Double lon, LocalDateTime createdDate) {
        this.id = id;
        this.buttonType = buttonType;
        this.text = text;
        this.lat = lat;
        this.lon = lon;
        this.createdDate = createdDate;
    }
}
