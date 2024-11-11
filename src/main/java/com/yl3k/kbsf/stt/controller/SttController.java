package com.yl3k.kbsf.stt.controller;

import com.yl3k.kbsf.global.response.response.ApiResponse;
import com.yl3k.kbsf.stt.dto.SttResponse;
import com.yl3k.kbsf.stt.service.SttService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stt")
public class SttController {

    private final SttService sttService;

    @PostMapping
    public ResponseEntity<ApiResponse<SttResponse>> speechToText(@RequestPart MultipartFile file) {

        SttResponse response = sttService.speechToText(file);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
