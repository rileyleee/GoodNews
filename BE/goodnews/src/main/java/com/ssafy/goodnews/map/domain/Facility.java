package com.ssafy.goodnews.map.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@NoArgsConstructor
@Setter
public class Facility {
    private Double 시설면적;
    private String 대피소종류;

    // ... getters, setters, etc.
}
