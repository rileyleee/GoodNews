package com.goodnews.member.member.domain;

import com.goodnews.member.member.dto.request.family.FamilyPlaceCanuseDto;
import com.goodnews.member.member.dto.request.family.FamilyPlaceUpdateRequestDto;
import com.goodnews.member.common.domain.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
@ToString
public class FamilyPlace extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private double lat;
    private double lon;
    private boolean canuse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id")
    private Family family;

    private int seq;
    @Builder
    public FamilyPlace(String name, double lat, double lon, boolean canuse, Family family,int seq) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.canuse = canuse;
        this.family = family;
        this.seq = seq+1;
    }

    public void updatePlaceInfo(FamilyPlaceUpdateRequestDto familyPlaceUpdateRequestDto) {
        this.name = familyPlaceUpdateRequestDto.getName();
        this.lat = familyPlaceUpdateRequestDto.getLat();
        this.lon= familyPlaceUpdateRequestDto.getLon();
    }

    public void updatePlaceCanuse(FamilyPlaceCanuseDto familyPlaceCanuseDto) {
        this.canuse = familyPlaceCanuseDto.isCanuse();
    }
}
