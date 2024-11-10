package com.yl3k.kbsf.global.response.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApiResponse<T> {

    private boolean success;
    private Response<T> response;

    public static <T> ApiResponse<T> success(T data) {

        return ApiResponse.<T>builder()
                .success(true)
                .response(new Response<>(data, null))
                .build();
    }

    public static ApiResponse<ErrorResponse> failure(ErrorResponse error) {

        return ApiResponse.<ErrorResponse>builder()
                .success(false)
                .response(new Response<>(null, error))
                .build();
    }

    @Getter
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Response<T> {

        private T data;
        private ErrorResponse error;
    }
}
