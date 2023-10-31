package com.ssafy.goodnews.map.dto.response;

import com.ssafy.goodnews.map.domain.Facility;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
public class MapResponseDto {

    private String type;
    private String name;
    private Double lat;
    private Double lon;
    private int canuse;
    private Facility facility;


    @Builder
    public MapResponseDto(String type, String name, Double lat, Double lon, int canuse, Facility facility) {
        this.type = type;
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.canuse = canuse;
        this.facility = facility;
    }
}