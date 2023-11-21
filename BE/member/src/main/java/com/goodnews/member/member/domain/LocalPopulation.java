package com.goodnews.member.member.domain;

import com.goodnews.member.member.dto.request.facility.LocalPopulationDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Entity
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

