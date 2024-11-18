package com.yl3k.kbsf.counsel.service;

import com.yl3k.kbsf.counsel.dto.WaitingCustomerDto;
import com.yl3k.kbsf.counsel.repository.WaitingQueueRepository;
import com.yl3k.kbsf.websocket.SocketEventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WaitingQueueService {

    private final WaitingQueueRepository waitingQueueRepository;
    private final SocketEventHandler socketEventHandler; // SocketEventHandler 주입

    public void addCustomer(WaitingCustomerDto customer) {
        // 대기열에 이미 존재하는지 확인
        List<WaitingCustomerDto> waitingQueues = waitingQueueRepository.getWaitingQueues(0);
        for (WaitingCustomerDto existingCustomer : waitingQueues) {
            if (existingCustomer.getUserId() == customer.getUserId()) {
                // 이미 대기열에 존재하면 예외 처리하거나 메서드 종료
                throw new IllegalArgumentException("Customer with userId " + customer.getUserId() + " already exists in the queue.");
            }
        }

        waitingQueueRepository.addCustomer(customer);
        sendQueueUpdate(); // 대기열 업데이트 후 실시간 전송
    }

    public List<WaitingCustomerDto> getWaitingQueues(Integer limit) {
        return waitingQueueRepository.getWaitingQueues(limit);
    }

    public WaitingCustomerDto assignCustomer() {
        WaitingCustomerDto assignedCustomer = waitingQueueRepository.assignCustomer();
        sendQueueUpdate(); // 대기열 상태 변경 후 전송
        return assignedCustomer;
    }

    public Integer getCustomerPosition(long userId) {
        List<WaitingCustomerDto> waitingList = waitingQueueRepository.getWaitingQueues(0);
        if (waitingList != null && !waitingList.isEmpty()) {
            for (int i = 0; i < waitingList.size(); i++) {
                if (waitingList.get(i).getUserId() == userId) {
                    return i + 1;
                }
            }
        }
        return null;
    }

    public boolean removeCustomer(long userId) {
        boolean result = waitingQueueRepository.removeCustomer(userId);
        if (result) {
            sendQueueUpdate(); // 대기열에서 제거 후 실시간 전송
        }
        return result;
    }

    // 대기열 상태를 전송하는 메서드
    private void sendQueueUpdate() {
        List<WaitingCustomerDto> waitingQueues = waitingQueueRepository.getWaitingQueues(0);
        socketEventHandler.broadcastQueueUpdate(waitingQueues); // 모든 클라이언트에게 대기열 상태를 전송
    }
}