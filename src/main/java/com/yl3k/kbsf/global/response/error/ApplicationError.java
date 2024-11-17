package com.yl3k.kbsf.global.response.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ApplicationError {

    TEST_ERROR(HttpStatus.BAD_REQUEST, "테스트 에러입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 에러가 발생했습니다."),

    MONGO_INSERT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "MongoDB insert 중 에러가 발생했습니다."),

    FILE_NAME_NULL(HttpStatus.BAD_REQUEST, "파일 이름은 null이 될 수 없습니다."),
    FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드 중 에러가 발생했습니다"),
    STT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "음성 파일 텍스트 전환 중 에러가 발생했습니다"),

    INSUFFICIENT_SEGMENTS(HttpStatus.BAD_REQUEST, "상담의 길이가 부족합니다."),
    PARSING_ERROR(HttpStatus.BAD_REQUEST, "JSON Parsing 중 에러가 발생했습니다."),
    OPENAI_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "OPENAI 호출 중 에러가 발생했습니다."),

    FIREBASE_NOTIFICATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알림 전송 중 에러가 발생했습니다."),
    GOOGLE_ACCESS_TOKEN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Google Access Token 요청 중 에러가 발생했습니다.");

    private final HttpStatus code;
    private final String message;
  
}