package com.yl3k.kbsf.summary.service;

import com.yl3k.kbsf.global.openai.service.OpenAiService;
import com.yl3k.kbsf.summary.dto.SummaryRequestDTO;
import com.yl3k.kbsf.summary.dto.SummaryResponseDTO;
import com.yl3k.kbsf.summary.entity.Summary;
import com.yl3k.kbsf.summary.repository.SummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Service
@Transactional
@RequiredArgsConstructor
public class SummaryService {

    private final SummaryRepository summaryRepository;
    private final OpenAiService openAiService;

    public SummaryResponseDTO createSummary(SummaryRequestDTO request){
        //요약 생성 - OpenAI API
        String prompt = "다음 대화를 요약해줘.\n"
                + "1. 한 줄로 요약. 제목느낌으로 명사형 요약\n"
                + "2. 1000자 이내로 요약\n"
                + "응답 형식은 한 줄로 요약한 내용을 먼저 적고, 줄바꿈을 한번만 하여 바로 아랫줄에 1000자 이내로 요약한 내용을 적어줘.\n\n"
                + request.getText();

        String openAiResponse = openAiService.askOpenAi(prompt);

        String[] summaries = openAiResponse.split("\n");

        summaries = Arrays.stream(summaries)
                .filter(s -> !s.trim().isEmpty()) // 빈 문자열 필터링
                .toArray(String[]::new); // 배열로 변환

        String summaryShort = summaries.length>0 ? summaries[0] : "잘못된 요약입니다.";
        String summaryText = summaries.length>1 ? summaries[1] : "잘못된 요약입니다.";

        //Summary 엔티티 저장
        Summary summary = Summary.builder()
                .summaryText(summaryText)
                .summaryShort(summaryShort)
                .build();
        summaryRepository.save(summary);

        //Summary 결과 반환
        return new SummaryResponseDTO(summaryText, summaryShort);
    }
}
