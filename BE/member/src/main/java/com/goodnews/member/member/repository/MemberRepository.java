package com.ssafy.goodnews.member.repository;

import com.ssafy.goodnews.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,String> {

    Optional<Member> findByIdAndPassword(String id, String password);

    Optional<Member> findByPhoneNumber(String phoneNumber);
}
