package com.yl3k.kbsf.counsel.service;

import com.yl3k.kbsf.counsel.dto.WaitingCustomerDto;
import com.yl3k.kbsf.counsel.repository.WaitingQueueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
}
