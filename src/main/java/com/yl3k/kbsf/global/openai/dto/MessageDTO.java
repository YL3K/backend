package com.yl3k.kbsf.global.openai.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MessageDTO {

    private String role;
    private String content;
}
