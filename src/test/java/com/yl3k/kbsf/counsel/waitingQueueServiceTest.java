package com.yl3k.kbsf.counsel;

import com.yl3k.kbsf.counsel.dto.WaitingCustomerDto;
import com.yl3k.kbsf.counsel.service.WaitingQueueService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
public class waitingQueueServiceTest {

    @Autowired
    private WaitingQueueService waitingQueueService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String QUEUE_KEY = "counseling_queue";

    @BeforeEach
    public void setUp() {
        redisTemplate.delete(QUEUE_KEY);
    }

    @AfterEach
    public void tearDown() {
        redisTemplate.delete(QUEUE_KEY);
    }

    @Test
    public void testAddCustomer(){
        WaitingCustomerDto customer = new WaitingCustomerDto(123, "윤다빈", Timestamp.valueOf(LocalDateTime.now()));
        waitingQueueService.addCustomer(customer);

        List<WaitingCustomerDto> result = waitingQueueService.getWaitingQueues(null);
        assertEquals(1, result.size());
        assertEquals(customer.getUserId(), result.get(0).getUserId());
    }

    @Test
    public void testGetWaitingQueues(){
        WaitingCustomerDto customer1 = new WaitingCustomerDto(123, "윤다빈", Timestamp.valueOf(LocalDateTime.now()));
        WaitingCustomerDto customer2 = new WaitingCustomerDto(321, "빈다윤", Timestamp.valueOf(LocalDateTime.now()));
        waitingQueueService.addCustomer(customer1);
        waitingQueueService.addCustomer(customer2);

        List<WaitingCustomerDto> result = waitingQueueService.getWaitingQueues(null);
        assertEquals(2, result.size());
        assertEquals(customer1.getUserId(), result.get(0).getUserId());
        assertEquals(customer2.getUserId(), result.get(1).getUserId());
    }

    @Test
    public void testGetWaiingQueusWithLimit(){
        for (int i = 1; i <= 10; i++) {
            waitingQueueService.addCustomer(new WaitingCustomerDto( i, "Customer " + i,  Timestamp.valueOf(LocalDateTime.now())));
        }
        List<WaitingCustomerDto> customers = waitingQueueService.getWaitingQueues(7);
        assertEquals(7, customers.size());
    }

    @Test
    public void testGetWaitingQueuesWhenQueueIsEmpty(){
        List<WaitingCustomerDto> result = waitingQueueService.getWaitingQueues(null);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testAssignCustomer(){
        WaitingCustomerDto customer = new WaitingCustomerDto(123, "윤다빈", Timestamp.valueOf(LocalDateTime.now()));
        waitingQueueService.addCustomer(customer);

        WaitingCustomerDto result = waitingQueueService.assignCustomer();
        assertEquals(customer.getUserId(), result.getUserId());
    }

    @Test
    public void testAssignCustomerWheneQueueIsEmpty(){
        WaitingCustomerDto result = waitingQueueService.assignCustomer();
        assertNull(result);
    }
}
