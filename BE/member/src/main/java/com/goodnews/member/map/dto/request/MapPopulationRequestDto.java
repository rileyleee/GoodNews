package com.goodnews.member.map.dto.request;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class MapPopulationRequestDto {

    List<LocalPopulationDto> populationList = new ArrayList<>();


}
