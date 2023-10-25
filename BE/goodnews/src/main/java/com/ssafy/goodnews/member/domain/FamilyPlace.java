package com.ssafy.goodnews.member.domain;

import com.ssafy.goodnews.common.domain.BaseEntity;
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

    @Builder
    public FamilyPlace(String name, double lat, double lon, boolean canuse, Family family) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.canuse = canuse;
        this.family = family;
    }
}
