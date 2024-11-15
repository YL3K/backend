package com.yl3k.kbsf.global.firebase.controller;

import com.yl3k.kbsf.global.firebase.dto.NotificationRequest;
import com.yl3k.kbsf.global.firebase.service.FirebaseService;
import com.yl3k.kbsf.global.response.dto.Message;
import com.yl3k.kbsf.global.response.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification")
public class FirebaseController {

    private final FirebaseService firebaseService;

    @PostMapping("/summary")
    public ResponseEntity<ApiResponse<Message>> sendSummaryCompleteNotice(@RequestBody NotificationRequest notificationRequest) throws IOException {

        firebaseService.sendSummaryCompleteNotification(notificationRequest);
        Message response = Message.builder().message("Summary complete notification send success").build();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/counsel")
    public ResponseEntity<ApiResponse<Message>> sendWaitingCompleteNotice(@RequestBody NotificationRequest notificationRequest) throws IOException {

        firebaseService.sendWaitingCompleteNotification(notificationRequest);
        Message response = Message.builder().message("Waiting complete notification send success").build();
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
