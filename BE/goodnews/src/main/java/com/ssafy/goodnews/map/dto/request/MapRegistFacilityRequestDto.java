package com.ssafy.goodnews.map.dto.request;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MapRegistFacilityRequestDto {

    private boolean buttonType;
    private String text;
    private Double lat;
    private Double lon;
}
