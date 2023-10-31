package com.ssafy.goodnews.map.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MapPopulationResponseDto {

    private int id;
    private String name;
    private int population;

    @Builder
    public MapPopulationResponseDto(int id,String name, int population) {
        this.id = id;
        this.name = name;
        this.population = population;
    }
}
