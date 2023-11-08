package com.goodnews.map.map.repository;


import com.goodnews.map.map.domain.LocalPopulation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface LocalPopulationRepository extends JpaRepository<LocalPopulation, Integer> {

}
