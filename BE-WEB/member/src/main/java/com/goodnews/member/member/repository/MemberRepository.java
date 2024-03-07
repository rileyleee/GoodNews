package com.goodnews.member.member.repository;

import com.goodnews.member.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,String> {

    Optional<Member> findByIdAndPassword(String id, String password);

    Optional<Member> findByPhoneNumber(String phoneNumber);

    List<Member> findByFamilyFamilyId(int familyId);
}
