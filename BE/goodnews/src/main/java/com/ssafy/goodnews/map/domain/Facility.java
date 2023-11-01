package com.ssafy.goodnews.map.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@NoArgsConstructor
@Document(collection = "goodnews")
public class Facility {
    private double 시설면적;
    private String 대피소종류;

    // ... getters, setters, etc.
}
