package com.goodnews.map.map.domain;


import com.goodnews.map.map.dto.request.MapRegistFacilityRequestDto;
import com.goodnews.map.util.BaseEntity;
import lombok.Builder;
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
public class FacilityState extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private Boolean buttonType;
    private String text;

    private Double lat;
    private Double lon;

    @Builder
    public FacilityState(MapRegistFacilityRequestDto mapRegistFacilityRequestDto) {
        this.buttonType = mapRegistFacilityRequestDto.isButtonType();
        this.text = mapRegistFacilityRequestDto.getText();
        this.lat = mapRegistFacilityRequestDto.getLat();
        this.lon = mapRegistFacilityRequestDto.getLon();
    }

    public void updateState(MapRegistFacilityRequestDto mapRegistFacilityRequestDto) {
        this.buttonType = mapRegistFacilityRequestDto.isButtonType();
        this.text = mapRegistFacilityRequestDto.getText();
    }
}
