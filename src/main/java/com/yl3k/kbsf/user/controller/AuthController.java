package com.yl3k.kbsf.user.controller;

import com.yl3k.kbsf.global.response.response.ApiResponse;
import com.yl3k.kbsf.user.dto.TokenDto;
import com.yl3k.kbsf.user.dto.UserRequestDto;
import com.yl3k.kbsf.user.dto.UserResponseDto;
import com.yl3k.kbsf.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserResponseDto>> signup(@RequestBody UserRequestDto requestDto) {
        return ResponseEntity.ok(ApiResponse.success(authService.signup(requestDto)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenDto>> login(@RequestBody UserRequestDto requestDto) {
        return ResponseEntity.ok(ApiResponse.success(authService.login(requestDto)));
    }
}