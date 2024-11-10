package com.yl3k.kbsf.record.controller;

import com.yl3k.kbsf.record.service.AnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/record/analysis")
public class AnalysisController {

    private final AnalysisService analysisService;
}
