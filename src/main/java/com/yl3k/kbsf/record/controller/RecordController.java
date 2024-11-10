package com.yl3k.kbsf.record.controller;

import com.yl3k.kbsf.record.service.RecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/record")
public class RecordController {

    private final RecordService recordService;
}
