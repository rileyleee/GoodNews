package com.ssafy.goodnews.common.exception.validator;

import com.ssafy.goodnews.common.exception.CustomException;
import com.ssafy.goodnews.common.exception.message.FamilyErrorEnum;
import com.ssafy.goodnews.member.domain.Family;
import com.ssafy.goodnews.member.domain.FamilyMember;
import com.ssafy.goodnews.member.domain.FamilyPlace;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Component
public class FamilyValidator {

    public void checkRegistFamily(Optional<FamilyMember> findMember, String familyId) {
        if (findMember.isPresent()) {
            throw CustomException.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .code(FamilyErrorEnum.INVALID_FAMILY.getCode())
                    .message(FamilyErrorEnum.INVALID_FAMILY.getMessage() + familyId)
                    .build();
        }

    }

    public void checkUpdateFamily(Optional<FamilyMember> findMember, String familyId) {
         if(findMember.isEmpty()) {
            throw CustomException.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .code(FamilyErrorEnum.INVALID_FAMILY_MEMBER.getCode())
                    .message(FamilyErrorEnum.INVALID_FAMILY_MEMBER.getMessage() + familyId)
                    .build();
        }

    }


    public void checkRegistOtherFamily(Optional<Family> findFamily, Optional<FamilyMember> findMember, String familyId) {

        if (findMember.isPresent()&&findFamily.isEmpty()) {
            throw CustomException.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .code(FamilyErrorEnum.INVALID_FAMILY_OTHER.getCode())
                    .message(FamilyErrorEnum.INVALID_FAMILY_OTHER.getMessage() + familyId)
                    .build();
        }

    }

    public void checkFamily(Optional<Family> findMember, String familyId) {
        if (findMember.isEmpty()) {
            throw CustomException.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .code(FamilyErrorEnum.INVALID_FAMILY_MEMBER.getCode())
                    .message(FamilyErrorEnum.INVALID_FAMILY_MEMBER.getMessage() + familyId)
                    .build();
        }
    }
    public void checkFamilyPlace(Optional<FamilyPlace> findMember) {
        if (findMember.isEmpty()) {
            throw CustomException.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .code(FamilyErrorEnum.INVALID_FAMILY_PLACE.getCode())
                    .message(FamilyErrorEnum.INVALID_FAMILY_PLACE.getMessage())
                    .build();
        }
    }
    public void checkFamilyPlaceList(List<FamilyPlace> aLlFamilyPlace) {
        if (aLlFamilyPlace.isEmpty()) {
            throw CustomException.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .code(FamilyErrorEnum.INVALID_FAMILY_PLACE.getCode())
                    .message(FamilyErrorEnum.INVALID_FAMILY_PLACE.getMessage())
                    .build();
        }
    }


}
