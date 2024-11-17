package com.yl3k.kbsf.counsel.dto;

import lombok.*;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class WaitingCustomerDto {
    private long userId;
    private String userName;
    private Timestamp startTime;
}
