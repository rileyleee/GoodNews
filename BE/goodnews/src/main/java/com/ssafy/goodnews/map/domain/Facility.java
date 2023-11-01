package com.ssafy.goodnews.map.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@NoArgsConstructor
public class Facility {

    @Field("시설면적")
    private double 시설면적;

    @Field("대피소종류")
    private String 대피소종류;

}