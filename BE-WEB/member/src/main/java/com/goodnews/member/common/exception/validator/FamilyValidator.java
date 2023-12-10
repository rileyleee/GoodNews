package com.goodnews.member.common.exception.validator;

import com.goodnews.member.common.exception.CustomException;
import com.goodnews.member.common.exception.message.FamilyErrorEnum;
import com.goodnews.member.member.domain.Family;
import com.goodnews.member.member.domain.FamilyMember;
import com.goodnews.member.member.domain.FamilyPlace;
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

    public void checkUpdateFamily(Optional<FamilyMember> findMember) {
        if(findMember.isEmpty()) {
            throw CustomException.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .code(FamilyErrorEnum.INVALID_FAMILY_MEMBER.getCode())
                    .message(FamilyErrorEnum.INVALID_FAMILY_MEMBER.getMessage())
                    .build();
        }

    }

    public void checkApproveFamily(Optional<FamilyMember> findMember, String familyId) {
        if(!findMember.get().isApprove()) {
            throw CustomException.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .code(FamilyErrorEnum.INVALID_FAMILY_APPROVE.getCode())
                    .message(FamilyErrorEnum.INVALID_FAMILY_APPROVE.getMessage() + familyId)
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

    public void checkFamily(Optional<FamilyMember> findMember, String familyId) {
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
    public void checkFamilyPlace(List<FamilyPlace> aLlFamilyPlace) {
        if (aLlFamilyPlace.size() >= 3) {
            throw CustomException.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .code(FamilyErrorEnum.INVALID_FAMILY_PLACE_SIZE.getCode())
                    .message(FamilyErrorEnum.INVALID_FAMILY_PLACE_SIZE.getMessage())
                    .build();
        }
    }


    public void checkSeq(int seq, String memberId) {
        if(seq > 3) {
            throw CustomException.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .code(FamilyErrorEnum.INVALID_FAMILY_PLACE_SIZE.getCode())
                    .message(FamilyErrorEnum.INVALID_FAMILY_PLACE_SIZE.getMessage()+memberId)
                    .build();
        }
    }
}
