package com.yl3k.kbsf.counsel.service;

import com.yl3k.kbsf.counsel.dto.WaitingCustomerDto;
import com.yl3k.kbsf.counsel.repository.WaitingQueueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WaitingQueueService {

    private final WaitingQueueRepository waitingQueueRepository;

    public void addCustomer(WaitingCustomerDto customer){
        waitingQueueRepository.addCustomer(customer);
    }

    public List<WaitingCustomerDto> getWaitingQueues(Integer limit){
        return waitingQueueRepository.getWaitingQueues(limit);
    }

    public WaitingCustomerDto assignCustomer(){
        return waitingQueueRepository.assignCustomer();
    }

    public Integer getCustomerPosition(long userId) {
        List<WaitingCustomerDto> waitingList = waitingQueueRepository.getWaitingQueues(0);
        if (waitingList != null && !waitingList.isEmpty()) {
            for (int i = 0; i < waitingList.size(); i++) {
                if (waitingList.get(i).getUserId() == userId) {
                    return i+1;
                }
            }
        }
        return null;
    }


    public boolean removeCustomer(long userId) {
        return waitingQueueRepository.removeCustomer(userId);
    }
}
