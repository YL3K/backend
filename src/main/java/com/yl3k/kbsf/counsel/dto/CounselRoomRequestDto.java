package com.yl3k.kbsf.counsel.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CounselRoomRequestDto {
    private long customerId;
    private long counselorId;
}
