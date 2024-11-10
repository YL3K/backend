package com.yl3k.kbsf.user.controller;

import com.yl3k.kbsf.global.response.dto.Message;
import com.yl3k.kbsf.global.response.error.ApplicationError;
import com.yl3k.kbsf.global.response.exception.ApplicationException;
import com.yl3k.kbsf.global.response.response.ApiResponse;
import com.yl3k.kbsf.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<Message>> test() {

        Message message = Message.builder().message("test message").build();
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    @GetMapping("/error")
    public ResponseEntity<ApiResponse<Message>> errorTest() {

        throw new ApplicationException(ApplicationError.TEST_ERROR);
    }
}
