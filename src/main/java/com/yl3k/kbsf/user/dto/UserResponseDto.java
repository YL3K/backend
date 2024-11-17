package com.yl3k.kbsf.user.dto;

import com.yl3k.kbsf.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseDto {
    private String loginId;
    private String userName;

    public static UserResponseDto of(User user) {
        return UserResponseDto.builder()
                .loginId(user.getLoginId())
                .userName(user.getUsername())
                .build();
    }
}