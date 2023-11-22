package com.goodnews.member.member.repository;

import com.goodnews.member.member.domain.FamilyPlace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FamilyPlaceRepository extends JpaRepository<FamilyPlace, Integer> {

    List<FamilyPlace> findByFamilyFamilyId(String familyId);
}
