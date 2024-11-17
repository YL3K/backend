package com.yl3k.kbsf.global.firebase.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {

    private String body;
    private String title;
}
