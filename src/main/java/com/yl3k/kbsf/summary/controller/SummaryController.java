package com.yl3k.kbsf.summary.controller;

import com.yl3k.kbsf.global.response.response.ApiResponse;
import com.yl3k.kbsf.summary.dto.SummaryRequestDTO;
import com.yl3k.kbsf.summary.dto.SummaryResponseDTO;
import com.yl3k.kbsf.summary.service.SummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/summary")
public class SummaryController {

    private final SummaryService summaryService;

    @PostMapping("/")
    public ResponseEntity<ApiResponse<SummaryResponseDTO>> createSummary(@RequestBody SummaryRequestDTO request) {
        SummaryResponseDTO response = summaryService.createSummary(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
