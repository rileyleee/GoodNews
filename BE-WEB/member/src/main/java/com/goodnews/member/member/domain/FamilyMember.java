package com.goodnews.member.member.domain;

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

    private String sendMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "received_member")
    private Member member;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id")
    private Family family;

    @Builder
    public FamilyMember(Member member, String sendMember, Family family) {
        this.member = member;
        this.sendMember = sendMember;
        this.family = family;
    }

    public void updateApprove() {
        this.approve = true;
    }
    public void updateFamily(Family family){
        this.family = family;
    }
}
