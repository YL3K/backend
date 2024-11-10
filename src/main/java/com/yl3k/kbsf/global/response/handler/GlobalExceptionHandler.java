package com.yl3k.kbsf.global.response.handler;

import com.yl3k.kbsf.global.response.error.ApplicationError;
import com.yl3k.kbsf.global.response.exception.ApplicationException;
import com.yl3k.kbsf.global.response.response.ApiResponse;
import com.yl3k.kbsf.global.response.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleApplicationException(ApplicationException e) {

        ApplicationError error = e.getError();
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(error.getCode().value())
                .message(error.getMessage())
                .build();

        ApiResponse<ErrorResponse> response = ApiResponse.failure(errorResponse);
        return new ResponseEntity<>(response, error.getCode());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleNoHandlerFoundException() {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message("존재하지 않는 리소스입니다.")
                .build();

        ApiResponse<ErrorResponse> response = ApiResponse.failure(errorResponse);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleException(Exception exception) {

        ApplicationError error = ApplicationError.INTERNAL_SERVER_ERROR;
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(error.getCode().value())
                .message(error.getMessage())
                .build();

        ApiResponse<ErrorResponse> response = ApiResponse.failure(errorResponse);
        return new ResponseEntity<>(response, error.getCode());
    }
}
