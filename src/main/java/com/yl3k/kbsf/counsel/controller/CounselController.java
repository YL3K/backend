package com.yl3k.kbsf.counsel.controller;

import com.yl3k.kbsf.counsel.service.CounselService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/counsel")
public class CounselController {

    private final CounselService counselService;
}
