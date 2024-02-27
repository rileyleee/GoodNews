package com.goodnews.member.member.domain;

import com.goodnews.member.member.dto.request.family.FamilyPlaceCanuseDto;
import com.goodnews.member.member.dto.request.family.FamilyPlaceUpdateRequestDto;
import com.goodnews.member.common.domain.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

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

    private String address;

    @CreatedDate
    public LocalDateTime createdDate;

    @Builder
    public FamilyPlace(String name, double lat, double lon, boolean canuse, Family family,int seq,String address) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.canuse = canuse;
        this.family = family;
        this.seq = seq;
        this.address = address;
    }

    public void updatePlaceInfo(FamilyPlaceUpdateRequestDto familyPlaceUpdateRequestDto) {
        this.name = familyPlaceUpdateRequestDto.getName();
        this.lat = familyPlaceUpdateRequestDto.getLat();
        this.lon= familyPlaceUpdateRequestDto.getLon();
        this.createdDate = LocalDateTime.now();
    }

    public void updatePlaceCanuse(FamilyPlaceCanuseDto familyPlaceCanuseDto) {
        this.canuse = familyPlaceCanuseDto.isCanuse();
        this.createdDate = LocalDateTime.now();
    }
}
