package com.ssafy.goodnews.member.repository;

import com.ssafy.goodnews.member.domain.FamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FamilyMemberRepository extends JpaRepository<FamilyMember,Integer> {

    Optional<FamilyMember> findByMemberIdAndFamilyFamilyId(String memberId, String familyId);
    Optional<FamilyMember> findByMemberId(String memberId);


}
