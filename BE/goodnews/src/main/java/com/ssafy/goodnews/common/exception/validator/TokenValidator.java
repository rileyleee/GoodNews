package com.ssafy.goodnews.common.exception.validator;

import com.ssafy.goodnews.common.exception.CustomException;
import com.ssafy.goodnews.common.exception.message.MemberErrorEnum;
import com.ssafy.goodnews.common.exception.message.TokenErrorEnum;
import com.ssafy.goodnews.member.domain.Member;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Component
public class TokenValidator {

    public void checkRedisRefreshToken(String refreshToken,String redisRefreshToken) {
        if (!refreshToken.equals(redisRefreshToken)) {
            throw CustomException.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .code(TokenErrorEnum.INVALID_REFRESHTOKEN.getCode())
                    .message(TokenErrorEnum.INVALID_REFRESHTOKEN.getMessage())
                    .build();
        }
    }

}
