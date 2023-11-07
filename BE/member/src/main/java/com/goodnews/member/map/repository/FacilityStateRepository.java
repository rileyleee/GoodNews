package com.goodnews.member.map.repository;

import com.goodnews.member.map.domain.FacilityState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FacilityStateRepository extends JpaRepository<FacilityState,Integer> {
    Optional<FacilityState> findByLatAndLon(Double lat, Double lon);
}
