package com.yl3k.kbsf.summary.controller;

import com.yl3k.kbsf.summary.service.SummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/summary")
public class SummaryController {

    private final SummaryService summaryService;
}
