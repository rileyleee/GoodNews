package com.goodnews.member.member.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@ToString
public class Family {

    @Id
    private String familyId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id") // 외래 키로 family_id를 사용
    private Member member;

    @OneToMany(mappedBy = "family", cascade = CascadeType.ALL)
    private List<FamilyPlace> familyPlaces;


    @Builder
    public Family(Member member) {
        this.familyId = member.getId();
    }
}
