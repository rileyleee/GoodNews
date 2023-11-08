package com.goodnews.map.map.repository;



import com.goodnews.map.map.domain.FacilityState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface FacilityStateRepository extends CrudRepository<FacilityState,Integer> {
    Optional<FacilityState> findByLatAndLon(Double lat, Double lon);
}
