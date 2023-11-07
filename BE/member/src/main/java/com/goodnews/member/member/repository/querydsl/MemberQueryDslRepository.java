package com.goodnews.member.member.repository.querydsl;


import com.goodnews.member.member.domain.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class MemberQueryDslRepository {
    private final JPAQueryFactory queryFactory;


    public Optional<FamilyMember> findFamilyMember(String memberId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(QFamilyMember.familyMember)
                .innerJoin(QFamilyMember.familyMember.member, QMember.member)
                .where(QMember.member.id.eq(memberId))
                .fetchOne());

    }

    public List<Member> findFamilyMemberList(String familyId, String memberId) {

        return  queryFactory
                .select(QMember.member)
                .from(QFamilyMember.familyMember)
                .innerJoin(QFamilyMember.familyMember.member, QMember.member)
                .innerJoin(QFamilyMember.familyMember.family, QFamily.family)
                .where(QFamily.family.familyId.eq(familyId).and(QFamilyMember.familyMember.approve.eq(true))
                        .and(QMember.member.id.ne(memberId)))
                .fetch();

    }

    public Optional<Family> findFamilyId(String memberId){
        return Optional.ofNullable(queryFactory
                .selectFrom(QFamily.family)
                .innerJoin(QFamilyMember.familyMember)
                .on(QFamilyMember.familyMember.member.id.eq(memberId))
                .fetchOne());
    }

    public List<FamilyPlace> findALlFamilyPlace(String memberId) {

        return queryFactory
                .selectFrom(QFamilyPlace.familyPlace)
                .innerJoin(QFamilyMember.familyMember)
                .on(QFamilyPlace.familyPlace.family.familyId.eq(QFamilyMember.familyMember.family.familyId))
                .innerJoin(QFamily.family)
                .on(QFamily.family.familyId.eq(QFamilyMember.familyMember.family.familyId))
                .where(QFamilyMember.familyMember.member.id.eq(memberId))
                .fetch();
    }



}
