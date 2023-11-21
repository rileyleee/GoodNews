package com.ssafy.goodnews.member.repository;

import com.ssafy.goodnews.member.domain.FamilyPlace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FamilyPlaceRepository extends JpaRepository<FamilyPlace,Integer> {

    List<FamilyPlace> findByFamilyFamilyId(String familyId);
}
