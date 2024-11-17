package com.yl3k.kbsf.counsel.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yl3k.kbsf.counsel.dto.WaitingCustomerDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;  // Lombok의 Slf4j 어노테이션을 추가
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
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
            log.error("Error serializing customer to JSON", e);
        }
    }

    public List<WaitingCustomerDto> getWaitingQueues(Integer limit) {
        int fetchLimit = (limit != null) ? limit : DEFAULT_LIMIT;
        List<String> customerJsonList = redisTemplate.opsForList().range(QUEUE_KEY, 0, fetchLimit-1);
        return customerJsonList.stream().map(json -> {
            try {
                return objectMapper.readValue(json, WaitingCustomerDto.class);
            } catch (JsonProcessingException e) {
                log.error("Error deserializing customer JSON", e);
                return null;
            }
        }).collect(Collectors.toList());
    }

    public WaitingCustomerDto assignCustomer() {
        String customerJson = redisTemplate.opsForList().leftPop(QUEUE_KEY);
        try {
            return customerJson != null ? objectMapper.readValue(customerJson, WaitingCustomerDto.class) : null;
        } catch (JsonProcessingException e) {
            log.error("Error deserializing assigned customer JSON", e);
            return null;
        }
    }

    public boolean removeCustomer(long userId) {
        try {
            List<String> customerJsonList = redisTemplate.opsForList().range(QUEUE_KEY, 0, -1);

            if (customerJsonList != null) {
                for (String customerJson : customerJsonList) {
                    WaitingCustomerDto customer = objectMapper.readValue(customerJson, WaitingCustomerDto.class);
                    if (customer.getUserId() == userId) {
                        String targetJson = objectMapper.writeValueAsString(customer);
                        Long removedCount = redisTemplate.opsForList().remove(QUEUE_KEY, 1, targetJson);
                        if (removedCount > 0) {
                            return true;
                        }
                    }
                }
            }
            return false;
        } catch (JsonProcessingException e) {
            log.error("Error processing customer data while removing userId: {}", userId, e);
        }
        return false;
    }
}