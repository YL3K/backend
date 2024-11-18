package com.yl3k.kbsf.summary.service;

import com.yl3k.kbsf.counsel.entity.CounselRoom;
import com.yl3k.kbsf.counsel.repository.CounselRoomRepository;
import com.yl3k.kbsf.global.openai.service.OpenAiService;
import com.yl3k.kbsf.stt.collection.FullText;
import com.yl3k.kbsf.stt.repository.FullTextRepository;
import com.yl3k.kbsf.summary.dto.SummaryRequestDTO;
import com.yl3k.kbsf.summary.dto.SummaryResponseDTO;
import com.yl3k.kbsf.summary.entity.Keyword;
import com.yl3k.kbsf.summary.entity.Summary;
import com.yl3k.kbsf.summary.entity.SummaryKeyword;
import com.yl3k.kbsf.summary.repository.KeywordRepository;
import com.yl3k.kbsf.summary.repository.SummaryKeywordRepository;
import com.yl3k.kbsf.summary.repository.SummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class SummaryService {

    private final SummaryRepository summaryRepository;
    private final OpenAiService openAiService;
    private final CounselRoomRepository counselRoomRepository;
    private final KeywordRepository keywordRepository;
    private final SummaryKeywordRepository summaryKeywordRepository;
    private final FullTextRepository fullTextRepository;

    public Map<String, String> createSummary(Long roomId){
        CounselRoom counselRoom = counselRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid room ID: " + roomId));

        // MongoDB에서 roomId를 사용해 text 조회
        FullText fullText = fullTextRepository.findByRoomId(roomId);
        if (fullText == null) {
            throw new IllegalArgumentException("Text not found for room ID: " + roomId);
        }

        String text = fullText.getFullText();

        //요약 생성 - OpenAI API
        String prompt = "다음 대화를 요약해줘.\n"
                + "1. 한 줄로 요약. 제목느낌으로 명사형 요약\n"
                + "2. 1000자 이내로 요약\n"
                + "응답 형식은 한 줄로 요약한 내용을 먼저 적고, 줄바꿈을 한번만 하여 바로 아랫줄에 1000자 이내로 요약한 내용을 적어줘.\n\n"
                + text;

        String openAiResponse = openAiService.askOpenAi(prompt);

        String[] summaries = openAiResponse.split("\n");

        summaries = Arrays.stream(summaries)
                .filter(s -> !s.trim().isEmpty()) // 빈 문자열 필터링
                .toArray(String[]::new); // 배열로 변환

        String summaryShort = summaries.length>0 ? summaries[0] : "잘못된 요약입니다.";
        String summaryText = summaries.length>1 ? summaries[1] : "잘못된 요약입니다.";

        //Summary 엔티티 저장
        Summary summary = Summary.builder()
                .counselRoom(counselRoom)
                .summaryText(summaryText)
                .summaryShort(summaryShort)
                .build();
        summaryRepository.save(summary);

        Map<String, String> response = new HashMap<>();
        response.put("summaryShort", summaryShort);
        response.put("summaryText", summaryText);

        return response;
    }

    public Map<String, Object> createKeywords(Long summaryId){
        //요약ID를 기반으로 Summary 조회
        Summary summary = summaryRepository.findById(summaryId)
                .orElseThrow(()->new IllegalArgumentException("Invalid summary ID: "+summaryId));

        //Summary_text를 기반으로 키워드 추출
        String summaryText = summary.getSummaryText();
        List<String> existingKeywords = keywordRepository.findAllKeywords();

        String prompt = "지정된 키워드 목록에서 요약문과 가장 관련성이 높은 최대 5개의 키워드를 추출해줘. 각 키워드를 콤마로 구분해서 제공해줘. 가장 중요한 키워드부터 순서대로 나열해줘. 관련된 키워드가 없다면 '관련된 키워드 없음'이라고 답해줘.\n\n"
                + "지정된 키워드: " + String.join(", ", existingKeywords) + "\n\n" + "요약: " + summaryText;

        String openAiResponse = openAiService.askOpenAi(prompt);

        // API응답 키워드 리스트로 변환
        String[] keywords = openAiResponse.split(",");
        keywords = Arrays.stream(keywords)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);

        //키워드 저장
        List<String> savedKeywords = new ArrayList<>();
        for(String keywordText : keywords){
            Keyword keyword = keywordRepository.findByKeyword(keywordText).orElse(null);
            if (keyword != null) { // 존재하는 키워드만 처리
                SummaryKeyword summaryKeyword = SummaryKeyword.builder()
                        .summary(summary)
                        .keyword(keyword)
                        .build();
                summaryKeywordRepository.save(summaryKeyword);

                savedKeywords.add(keywordText);
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", Map.of("keywords", savedKeywords));

        return response;
    }
}
