package com.goodnews.member.member.domain;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@ToString
public class Family {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int familyId;

    @OneToMany(mappedBy = "family", cascade = CascadeType.ALL)
    private List<FamilyPlace> familyPlaces;
}
