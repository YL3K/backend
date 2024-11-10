package com.yl3k.kbsf.record.service;

import com.yl3k.kbsf.record.repository.FeedbackRepository;
import com.yl3k.kbsf.record.repository.MemoRepository;
import com.yl3k.kbsf.summary.repository.SummaryKeywordRepository;
import com.yl3k.kbsf.summary.repository.SummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RecordService {

    private final SummaryRepository summaryRepository;
    private final SummaryKeywordRepository summaryKeywordRepository;
    private final FeedbackRepository feedbackRepository;
    private final MemoRepository memoRepository;
}
