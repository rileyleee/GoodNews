package com.goodnews.member.member.repository.querydsl;


import com.goodnews.member.member.domain.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.goodnews.member.member.domain.QFamily.*;
import static com.goodnews.member.member.domain.QFamilyMember.*;
import static com.goodnews.member.member.domain.QFamilyPlace.*;
import static com.goodnews.member.member.domain.QMember.*;

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

    public List<Member> findFamilyMemberList(String familyId, String memberId) {

        return  queryFactory
                .select(member)
                .from(familyMember)
                .innerJoin(familyMember.member, member)
                .innerJoin(familyMember.family, family)
                .where(family.familyId.eq(familyId).and(familyMember.approve.eq(true))
                        .and(member.id.ne(memberId)))
                .fetch();

    }

    public Optional<FamilyMember> findFamilyId(String memberId){
        return Optional.ofNullable(queryFactory
                .selectFrom(familyMember)
                .where(familyMember.member.id.eq(memberId))
                .fetchOne());
    }



    public List<FamilyPlace> findALlFamilyPlace(String memberId) {

        return queryFactory
                .selectFrom(familyPlace)
                .innerJoin(familyMember)
                .on(familyPlace.family.familyId.eq(familyMember.family.familyId))
                .innerJoin(family)
                .on(family.familyId.eq(familyMember.family.familyId))
                .where(familyMember.member.id.eq(memberId))
                .fetch();
    }



}
