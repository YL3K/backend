package com.yl3k.kbsf.counsel.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yl3k.kbsf.counsel.dto.WaitingCustomerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
public class WaitingQueueRepository {
    private static final String QUEUE_KEY = "counseling_queue";
    private static final int DEFAULT_LIMIT = 6;

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public void addCustomer(WaitingCustomerDto customer) {
        try {
            String customerJson = objectMapper.writeValueAsString(customer);
            redisTemplate.opsForList().rightPush(QUEUE_KEY, customerJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public List<WaitingCustomerDto> getWaitingQueues(Integer limit) {
        int fetchLimit = (limit != null) ? limit : DEFAULT_LIMIT;
        List<String> customerJsonList = redisTemplate.opsForList().range(QUEUE_KEY, 0, fetchLimit-1);
        return customerJsonList.stream().map(json -> {
            try {
                return objectMapper.readValue(json, WaitingCustomerDto.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return null;
            }
        }).collect(Collectors.toList());
    }

    public WaitingCustomerDto assignCustomer() {
        String customerJson = redisTemplate.opsForList().leftPop(QUEUE_KEY);
        try {
            return customerJson != null ? objectMapper.readValue(customerJson, WaitingCustomerDto.class) : null;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
