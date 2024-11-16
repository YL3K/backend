package com.yl3k.kbsf.global.firebase.controller;

import com.yl3k.kbsf.global.response.dto.Message;
import com.yl3k.kbsf.global.response.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GoogleController {

    @GetMapping("/authcode")
    public ResponseEntity<ApiResponse<Message>> authCode() {

        Message message = Message.builder().message("success").build();
        return ResponseEntity.ok(ApiResponse.success(message));
    }
}
