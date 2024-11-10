package com.yl3k.kbsf.summary.service;

import com.yl3k.kbsf.summary.repository.SummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SummaryService {

    private final SummaryRepository summaryRepository;
}
