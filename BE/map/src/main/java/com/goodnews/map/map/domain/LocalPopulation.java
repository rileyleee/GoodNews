package com.goodnews.map.map.domain;

import com.goodnews.map.map.dto.request.LocalPopulationDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@NoArgsConstructor
@ToString
public class LocalPopulation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private int population;
    public void updatePopulation(LocalPopulationDto localPopulationDto) {
        this.population = localPopulationDto.getPopulation();
    }
}

