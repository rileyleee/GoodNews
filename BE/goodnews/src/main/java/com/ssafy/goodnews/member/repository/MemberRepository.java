package com.ssafy.goodnews.member.repository;

import com.ssafy.goodnews.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member,String> {
}
