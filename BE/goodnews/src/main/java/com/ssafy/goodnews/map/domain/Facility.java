package com.ssafy.goodnews.map.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Facility {

    private double 시설면적;
    private String 대피소종류;

    public Facility(String input) {
        // 예: input = "100.5,공공대피소"
        String[] parts = input.split(",");
        this.시설면적 = Double.parseDouble(parts[0]);
        this.대피소종류 = parts[1];
    }
}
