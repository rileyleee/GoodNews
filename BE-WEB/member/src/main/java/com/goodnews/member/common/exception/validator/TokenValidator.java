package com.goodnews.member.common.exception.validator;

import com.goodnews.member.common.exception.CustomException;
import com.goodnews.member.common.exception.message.TokenErrorEnum;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

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

    public void checkTokenIsNull(String token) {
        if (token == null) {
            throw CustomException.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .code(TokenErrorEnum.INVALID_TOKEN.getCode())
                    .message(TokenErrorEnum.INVALID_TOKEN.getMessage())
                    .build();
        }
    }

}
