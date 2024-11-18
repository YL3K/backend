package com.yl3k.kbsf.stt.controller;

import com.yl3k.kbsf.global.response.dto.Message;
import com.yl3k.kbsf.global.response.response.ApiResponse;
import com.yl3k.kbsf.stt.dto.SttResponse;
import com.yl3k.kbsf.stt.service.SttService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stt")
@Slf4j
public class SttController {

    private final SttService sttService;

    @PostMapping
    public ResponseEntity<ApiResponse<SttResponse>> speechToText(@RequestPart MultipartFile file, @RequestPart Long roomId) {

        SttResponse response = sttService.speechToText(file, roomId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/test")
    public ResponseEntity<ApiResponse<Message>> test(@RequestPart MultipartFile file, @RequestPart Long roomId) {

        log.info("@RequestPart(MultipartFile, Long) test success");
        Message response = Message.builder().message("@RequestPart(MultipartFile, Long) test success").build();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/test/long")
    public ResponseEntity<ApiResponse<Message>> test(@RequestPart Long roomId) {

        log.info("@RequestPart(Long) test success");
        Message response = Message.builder().message("@RequestPart(Long) test success").build();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/test/file")
    public ResponseEntity<ApiResponse<Message>> test(@RequestPart MultipartFile file) {

        log.info("@RequestPart(MultipartFile) test success");
        Message response = Message.builder().message("@RequestPart(MultipartFile) test success").build();
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
