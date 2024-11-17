package com.yl3k.kbsf.global.openai.service;

import com.yl3k.kbsf.global.openai.dto.MessageDTO;
import com.yl3k.kbsf.global.openai.dto.OpenAiRequest;
import com.yl3k.kbsf.global.openai.dto.OpenAiResponse;
import com.yl3k.kbsf.global.response.error.ApplicationError;
import com.yl3k.kbsf.global.response.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OpenAiService {

    @Value("${openai.model}")
    private String model;
    private final RestTemplate openAiRestTemplate;

    public String askOpenAi(String prompt) {
        String url = "https://api.openai.com/v1/chat/completions";

        MessageDTO message = MessageDTO.builder()
                .role("user")
                .content(prompt)
                .build();

        List<MessageDTO> messageList = new ArrayList<>();
        messageList.add(message);

        OpenAiRequest request = OpenAiRequest.builder()
                .model(model)
                .messages(messageList)
                .maxTokens(200)
                .build();

        OpenAiResponse response = openAiRestTemplate.postForObject(url, request, OpenAiResponse.class);

        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
            throw new ApplicationException(ApplicationError.OPENAI_ERROR);
        }

        return response.getChoices().get(0).getMessage().getContent();
    }
}