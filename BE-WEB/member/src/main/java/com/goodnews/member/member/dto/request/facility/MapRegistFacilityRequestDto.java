package com.goodnews.member.member.dto.request.facility;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MapRegistFacilityRequestDto {

    private String id;
    private boolean buttonType;
    private String text;
    private Double lat;
    private Double lon;
    private String date;
}
