package com.yl3k.kbsf.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenDto {
    private Integer userId;
    private String userType;
    private String grantType;
    private String accessToken;
    private Long tokenExpiresIn;
}