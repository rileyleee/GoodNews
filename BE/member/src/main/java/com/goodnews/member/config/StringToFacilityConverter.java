package com.goodnews.member.config;

import com.goodnews.member.map.domain.Facility;
import org.json.JSONException;
import org.springframework.core.convert.converter.Converter;
import org.json.JSONObject;

public class StringToFacilityConverter implements Converter<String, Facility> {

    @Override
    public Facility convert(String source) {
        try {
            if (source == null || source.trim().isEmpty()) {
                return null; // 또는 적절한 기본값 반환
            }
            JSONObject jsonObject = new JSONObject(source);
            Facility facility = new Facility();
            facility.set시설면적(jsonObject.getDouble("시설면적"));
            facility.set대피소종류(jsonObject.getString("대피소종류"));
            return facility;
        } catch (JSONException e) {
            throw new IllegalArgumentException("Failed to convert string to Facility: " + source, e);
        }
    }
}
