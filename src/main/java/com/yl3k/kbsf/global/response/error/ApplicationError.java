package com.yl3k.kbsf.global.response.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ApplicationError {

    TEST_ERROR(HttpStatus.BAD_REQUEST, "테스트 에러입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 에러가 발생했습니다."),
    OPENAI_ERROR(HttpStatus.BAD_REQUEST, "OpenAI API 호출 중 오류가 발생했습니다.");

    private final HttpStatus code;
    private final String message;
}
