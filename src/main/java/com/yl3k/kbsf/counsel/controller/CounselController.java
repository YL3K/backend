package com.yl3k.kbsf.counsel.controller;

import com.yl3k.kbsf.counsel.dto.WaitingCustomerDto;
import com.yl3k.kbsf.counsel.service.CounselService;
import com.yl3k.kbsf.counsel.service.WaitingQueueService;
import com.yl3k.kbsf.global.response.response.ApiResponse;
import com.yl3k.kbsf.global.response.response.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/counsel")
public class CounselController {

    private final CounselService counselService;
    private final WaitingQueueService waitingQueueService;

    @PostMapping("/queue")
    public ResponseEntity<ApiResponse<WaitingCustomerDto>> addCustomer(@RequestBody WaitingCustomerDto waitingCustomerDto){
        waitingCustomerDto.setStartTime(Timestamp.valueOf(LocalDateTime.now()));
        waitingQueueService.addCustomer(waitingCustomerDto);

        return ResponseEntity.ok(ApiResponse.success(waitingCustomerDto));
    }

    @GetMapping("/queue")
    public ResponseEntity<ApiResponse<?>> getWaitingQueues(@RequestParam(value = "limit", required = false) Integer limit){
        List<WaitingCustomerDto> waitingQueues = waitingQueueService.getWaitingQueues(limit);

        if (!waitingQueues.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.success(waitingQueues));
        } else {
            ErrorResponse errorResponse = new ErrorResponse(204, "No customers in the waiting queue.");
            return ResponseEntity.ok(ApiResponse.failure(errorResponse));
        }
    }

    @PostMapping("/queue/assign")
    public ResponseEntity<ApiResponse<?>> assignCustomer(){
        WaitingCustomerDto assignedCustomer = waitingQueueService.assignCustomer();

        if (assignedCustomer != null) {
            return ResponseEntity.ok(ApiResponse.success(assignedCustomer));
        } else {
            ErrorResponse errorResponse = new ErrorResponse(204, "No customers in the waiting queue.");
            return ResponseEntity.ok(ApiResponse.failure(errorResponse));
        }
    }

    @GetMapping("/queue/position")
    public ResponseEntity<ApiResponse<?>> getCustomerPosition(@RequestBody WaitingCustomerDto waitingCustomerDto) {
        long userId = waitingCustomerDto.getUserId();
        Integer position = waitingQueueService.getCustomerPosition(userId);
        if (position != null) {
            return ResponseEntity.ok(ApiResponse.success(position));
        } else {
            ErrorResponse errorResponse = new ErrorResponse(204, userId + " customers in the waiting queue.");
            return ResponseEntity.ok(ApiResponse.failure(errorResponse));
        }
    }

    @DeleteMapping("/queue")
    public ResponseEntity<ApiResponse<?>> removeCustomer(@RequestBody WaitingCustomerDto waitingCustomerDto) {
        long userId = waitingCustomerDto.getUserId();
        boolean success = waitingQueueService.removeCustomer(userId);
        if (success) {
            return ResponseEntity.ok(ApiResponse.success("Customer removed successfully"));
        } else {
            ErrorResponse errorResponse = new ErrorResponse(204, userId + " customers in the waiting queue.");
            return ResponseEntity.ok(ApiResponse.failure(errorResponse));
        }
    }
}
