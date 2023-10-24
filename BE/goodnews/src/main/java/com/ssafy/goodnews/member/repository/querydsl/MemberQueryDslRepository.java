package com.ssafy.goodnews.member.repository.querydsl;


import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.goodnews.member.domain.FamilyMember;
import com.ssafy.goodnews.member.domain.QFamilyMember;
import com.ssafy.goodnews.member.domain.QMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.ssafy.goodnews.member.domain.QFamilyMember.*;
import static com.ssafy.goodnews.member.domain.QMember.*;

@RequiredArgsConstructor
@Repository
public class MemberQueryDslRepository {
    private final JPAQueryFactory queryFactory;


    public Optional<FamilyMember> findFamilyMember(String memberId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(familyMember)
                .innerJoin(familyMember.member, member)
                .where(member.id.eq(memberId))
                .fetchOne());

    }



}
