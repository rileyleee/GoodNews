package com.ssafy.goodnews.common.exception.validator;

import com.ssafy.goodnews.common.exception.CustomException;
import com.ssafy.goodnews.common.exception.message.MemberErrorEnum;
import com.ssafy.goodnews.member.domain.FamilyMember;
import com.ssafy.goodnews.member.domain.Member;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Component
public class MemberValidator {

    public void checkMember(Optional<Member> member, String memberId) {
        if (member.isEmpty()) {
            throw CustomException.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .code(MemberErrorEnum.INVALID_MEMBER.getCode())
                    .message(MemberErrorEnum.INVALID_MEMBER.getMessage() + memberId)
                    .build();
        }
    }



}
