package com.yl3k.kbsf.stt.controller;

import com.yl3k.kbsf.stt.service.SttService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stt")
public class SttController {

    private final SttService sttService;
}
