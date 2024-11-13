package com.yl3k.kbsf.counsel.controller;

import com.yl3k.kbsf.counsel.dto.WaitingCustomerDto;
import com.yl3k.kbsf.counsel.service.CounselService;
import com.yl3k.kbsf.counsel.service.WaitingQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/counsel")
public class CounselController {

    private final CounselService counselService;
    private final WaitingQueueService waitingQueueService;

    @PostMapping("/queue")
    public ResponseEntity<Map<String, Object>> addCustomer(@RequestBody WaitingCustomerDto waitingCustomerDto){
        waitingCustomerDto.setStartTime(Timestamp.valueOf(LocalDateTime.now()));
        waitingQueueService.addCustomer(waitingCustomerDto);

        Map<String, Object> response = new HashMap<>();
        Map<String, Object> responseData = new HashMap<>();

        responseData.put("data", waitingCustomerDto);
        response.put("success", true);
        response.put("response", responseData);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/queue")
    public ResponseEntity<Map<String, Object>> getWaitingQueues(@RequestParam(value = "limit", required = false) Integer limit){
        List<WaitingCustomerDto> waitingQueues = waitingQueueService.getWaitingQueues(limit);
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> responseData1 = new HashMap<>();
        Map<String, Object> responseData2 = new HashMap<>();
        System.out.println(waitingQueues);

        if (!waitingQueues.isEmpty()) {
            responseData2.put("queue", waitingQueues);
            responseData1.put("data", responseData2);
            response.put("success", true);
            response.put("response", responseData1);
        } else {
            responseData1.put("code", 204);
            responseData1.put("message", "No customers in the waiting queue.");
            response.put("success", false);
            response.put("error", responseData1);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/queue/assign")
    public ResponseEntity<Map<String, Object>> assignCustomer(){
        WaitingCustomerDto assignedCustomer = waitingQueueService.assignCustomer();
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> responseData = new HashMap<>();

        if (assignedCustomer != null) {
            responseData.put("data", assignedCustomer);
            response.put("success", true);
            response.put("response", responseData);
        } else {
            responseData.put("code", 204);
            responseData.put("message", "No customers in the waiting queue.");
            response.put("success", false);
            response.put("error", responseData);
        }
        return ResponseEntity.ok(response);
    }
}
