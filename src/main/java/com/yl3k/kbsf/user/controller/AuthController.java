package com.yl3k.kbsf.user.controller;

import com.yl3k.kbsf.global.response.dto.Message;
import com.yl3k.kbsf.global.response.response.ApiResponse;
import com.yl3k.kbsf.global.response.response.ErrorResponse;
import com.yl3k.kbsf.user.dto.TokenDto;
import com.yl3k.kbsf.user.dto.UserRequestDto;
import com.yl3k.kbsf.user.dto.UserResponseDto;
import com.yl3k.kbsf.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/check/loginid")
    public ResponseEntity<ApiResponse<?>> checkLoginId(@RequestParam(name = "loginId") String loginId) {
        boolean isAvailable = authService.isLoginIdAvailable(loginId);
        Message successMessage = Message.builder().message("사용 가능한 아이디입니다").build();
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(409)
                .message("이미 사용 중인 아이디입니다")
                .build();
        if (isAvailable) {
            return ResponseEntity.ok(ApiResponse.success(successMessage));
        } else {
            return ResponseEntity.ok(ApiResponse.failure(errorResponse));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenDto>> login(@RequestBody UserRequestDto requestDto) {
        return ResponseEntity.ok(ApiResponse.success(authService.login(requestDto)));
    }
}