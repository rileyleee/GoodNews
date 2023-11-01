package com.ssafy.goodnews.map.repository;

import com.ssafy.goodnews.map.domain.FacilityState;
import com.ssafy.goodnews.map.dto.request.MapRegistFacilityRequestDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FacilityStateRepository extends JpaRepository<FacilityState,Integer> {
    Optional<FacilityState> findByLatAndLon(Double lat, Double lon);
}
