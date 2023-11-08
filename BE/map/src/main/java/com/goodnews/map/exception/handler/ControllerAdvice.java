package com.goodnews.map.exception.handler;


import com.fasterxml.jackson.core.JsonProcessingException;

import com.goodnews.map.exception.CustomException;
import com.goodnews.map.util.ExceptionResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(CustomException.class)
    private ResponseEntity<ExceptionResponseDto> exception(CustomException e) {

        return ResponseEntity
                .status(e.getStatus())
                .body(ExceptionResponseDto.builder()
                        .code(e.getCode())
                        .message(e.getMessage())
                        .build());
    }

    @ExceptionHandler(JsonProcessingException.class)
    private ResponseEntity<ExceptionResponseDto> JsonException(JsonProcessingException e) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponseDto.builder()
                        .code(-1)
                        .message("JSON 처리 중 문제가 생겼습니다")
                        .build());
    }

}
