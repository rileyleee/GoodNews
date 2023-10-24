package com.ssafy.goodnews.member.repository;

import com.ssafy.goodnews.member.domain.Family;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FamilyRepository extends JpaRepository<Family,String> {
}
