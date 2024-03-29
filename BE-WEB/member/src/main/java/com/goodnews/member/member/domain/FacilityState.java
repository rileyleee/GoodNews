package com.goodnews.member.member.domain;

import com.goodnews.member.member.dto.request.facility.MapRegistFacilityRequestDto;
import com.goodnews.member.common.domain.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Entity
@NoArgsConstructor
@ToString
public class FacilityState extends BaseEntity {

    @Id
    private String id;
    private Boolean buttonType;
    private String text;

    private Double lat;
    private Double lon;

    private LocalDateTime createdDate;

    @Builder
    public FacilityState(MapRegistFacilityRequestDto mapRegistFacilityRequestDto) {
        this.id = mapRegistFacilityRequestDto.getId();
        this.buttonType = mapRegistFacilityRequestDto.isButtonType();
        this.text = mapRegistFacilityRequestDto.getText();
        this.lat = mapRegistFacilityRequestDto.getLat();
        this.lon = mapRegistFacilityRequestDto.getLon();
        this.createdDate = LocalDateTime.parse(mapRegistFacilityRequestDto.getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public void updateState(MapRegistFacilityRequestDto mapRegistFacilityRequestDto) {
        this.buttonType = mapRegistFacilityRequestDto.isButtonType();
        this.text = mapRegistFacilityRequestDto.getText();
        this.createdDate = LocalDateTime.parse(mapRegistFacilityRequestDto.getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
