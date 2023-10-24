package com.ssafy.goodnews.member.domain;

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
    private String family_id;

    @OneToOne
    @PrimaryKeyJoinColumn
    private Member member;

    @OneToMany(mappedBy = "family", cascade = CascadeType.ALL)
    private List<FamilyPlace> familyPlaces;
}
