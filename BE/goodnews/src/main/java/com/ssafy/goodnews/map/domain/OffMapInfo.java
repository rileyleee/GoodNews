package com.ssafy.goodnews.map.domain;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.Id;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@Getter
@NoArgsConstructor
@Document(collection = "goodnews")
public class OffMapInfo {

    @Id
    private String id;
    private String name;
    private Double lon;
    private Double lat;
    private int canuse;
//    private Map<String, Object> facility;

    @Field("facility")
    private String facilityJson;

    public Map<String, Object> getFacilityAsMap() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(facilityJson, new TypeReference<Map<String, Object>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }
}
