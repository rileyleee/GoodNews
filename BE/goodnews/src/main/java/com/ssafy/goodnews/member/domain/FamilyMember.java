package com.ssafy.goodnews.member.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
@ToString
public class FamilyMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private boolean approve;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phone")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id")
    private Family family;

    @Builder
    public FamilyMember(Member member, Family family) {
        this.member = member;
        this.family = family;
    }
}
