package com.ssafy.goodnews.map.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class FacilityStateResponseDto {

    private int id;
    private Boolean buttonType;
    private String text;

    private Double lat;
    private Double lon;

    private LocalDateTime lastModifiedDate;

    @Builder
    public FacilityStateResponseDto(int id, Boolean buttonType, String text, Double lat, Double lon, LocalDateTime lastModifiedDate) {
        this.id = id;
        this.buttonType = buttonType;
        this.text = text;
        this.lat = lat;
        this.lon = lon;
        this.lastModifiedDate = lastModifiedDate;
    }
}
