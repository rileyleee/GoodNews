package com.goodnews.member.member.repository;

import com.goodnews.member.member.domain.FamilyMember;
import com.goodnews.member.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FamilyMemberRepository extends JpaRepository<FamilyMember, Integer> {

    Optional<FamilyMember> findByMemberIdAndFamilyFamilyId(String memberId, int familyId);
    Optional<FamilyMember> findByMemberId(String memberId);

    Optional<FamilyMember> findByMemberIdAndApproveIsTrue(String memberId);

    List<FamilyMember> findByMemberIdAndApproveIsFalse(String memberId);

    List<FamilyMember> findByFamilyFamilyId(int familyId);
}
