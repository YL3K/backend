package com.yl3k.kbsf.counsel.controller;

import com.yl3k.kbsf.counsel.dto.WaitingCustomerDto;
import com.yl3k.kbsf.counsel.service.CounselService;
import com.yl3k.kbsf.counsel.service.WaitingQueueService;
import com.yl3k.kbsf.global.response.response.ApiResponse;
import com.yl3k.kbsf.global.response.response.ErrorResponse;
import com.yl3k.kbsf.user.entity.User;
import com.yl3k.kbsf.user.repository.UserRepository;
import com.yl3k.kbsf.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/counsel")
public class CounselController {
    private final AuthService authService;
    private final CounselService counselService;
    private final WaitingQueueService waitingQueueService;

    // 고객 대기열 입장
    @PostMapping("/queue")
    public ResponseEntity<ApiResponse<?>> addCustomer(){
        User user = authService.getCurrentUser();
        WaitingCustomerDto waitingCustomerDto = new WaitingCustomerDto(user.getUserId(), user.getUsername(),Timestamp.valueOf(LocalDateTime.now()));

        // userType이 "customer"일 때만 실행
        if (!"customer".equals(user.getUserType().toString())) {
            return ResponseEntity.status(400).body(ApiResponse.failure(new ErrorResponse(400, "Only customers can be added to the queue.")));
        }

        try {
            waitingQueueService.addCustomer(waitingCustomerDto);
            return ResponseEntity.ok(ApiResponse.success(waitingCustomerDto));
        } catch (IllegalArgumentException e) {
            // 이미 대기열에 존재하는 경우 오류 메시지 반환
            return ResponseEntity.status(400).body(ApiResponse.failure(new ErrorResponse(400, e.getMessage())));
        }
    }

    // 대기열 목록 조회
    @GetMapping("/queue")
    public ResponseEntity<ApiResponse<?>> getWaitingQueues(@RequestParam(value = "limit", required = false) Integer limit){
        List<WaitingCustomerDto> waitingQueues = waitingQueueService.getWaitingQueues(limit);

        if (!waitingQueues.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.success(waitingQueues));
        } else {
            return ResponseEntity.status(204).body(ApiResponse.failure(new ErrorResponse(204, "No customers in the waiting queue.")));
        }
    }

    // 상담사 대기열 배정
    @PostMapping("/queue/assign")
    public ResponseEntity<ApiResponse<?>> assignCustomer(){
        User user = authService.getCurrentUser();

        // userType이 "counselor"일 때만 실행
        if (!"counselor".equals(user.getUserType().toString())) {
            return ResponseEntity.status(400).body(ApiResponse.failure(new ErrorResponse(400, "Only counselors can be assigned to the queue.")));
        }

        WaitingCustomerDto assignedCustomer = waitingQueueService.assignCustomer();

        if (assignedCustomer != null) {
            return ResponseEntity.ok(ApiResponse.success(assignedCustomer));
        } else {
            return ResponseEntity.status(204).body(ApiResponse.failure(new ErrorResponse(204, "No customers in the waiting queue.")));
        }
    }

    // 고객 대기열 순서 조회
    @GetMapping("/queue/position")
    public ResponseEntity<ApiResponse<?>> getCustomerPosition() {
        User user = authService.getCurrentUser();

        // userType이 "customer"일 때만 실행
        if (!"customer".equals(user.getUserType().toString())) {
            return ResponseEntity.status(400).body(ApiResponse.failure(new ErrorResponse(400, "Only customers can be added to the queue.")));
        }

        Integer position = waitingQueueService.getCustomerPosition(user.getUserId());

        if (position != null) {
            return ResponseEntity.ok(ApiResponse.success(position));
        } else {
            return ResponseEntity.status(204).body(ApiResponse.failure(new ErrorResponse(204, user.getUserId() + " customers in the waiting queue.")));
        }
    }

    // 고객 대기열 삭제
    @DeleteMapping("/queue")
    public ResponseEntity<ApiResponse<?>> removeCustomer(@RequestParam("userId") String userId) {
        User user = authService.getCurrentUser();

        // userType이 "customer"이면 본인 대기열만 삭제 가능, "counselor"이면, 고객 대기열 삭제 가능
        if ("customer".equals(user.getUserType().toString())) {
            if (!user.getUserId().equals(Integer.parseInt(userId))) {
                return ResponseEntity.status(401).body(ApiResponse.failure(new ErrorResponse(401, "You do not have permission to perform this action.")));
            }
        }

        boolean success = waitingQueueService.removeCustomer(Integer.parseInt(userId));

        if (success) {
            return ResponseEntity.ok(ApiResponse.success("Customer removed successfully"));
        } else {
            return ResponseEntity.status(204).body(ApiResponse.failure(new ErrorResponse(204, user.getUserId() + " customers in the waiting queue.")));
        }
    }
}
