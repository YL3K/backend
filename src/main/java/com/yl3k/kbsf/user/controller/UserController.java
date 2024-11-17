package com.yl3k.kbsf.user.controller;

import com.yl3k.kbsf.global.response.dto.Message;
import com.yl3k.kbsf.global.response.error.ApplicationError;
import com.yl3k.kbsf.global.response.exception.ApplicationException;
import com.yl3k.kbsf.global.response.response.ApiResponse;
import com.yl3k.kbsf.user.dto.UserRequestDto;
import com.yl3k.kbsf.user.dto.UserResponseDto;
import com.yl3k.kbsf.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getMyMemberInfo() {
        UserResponseDto myInfoBySecurity = userService.getMyInfoBySecurity();
        System.out.println(myInfoBySecurity.getUserName());
        return ResponseEntity.ok((myInfoBySecurity));
        // return ResponseEntity.ok(memberService.getMyInfoBySecurity());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Message>> test() {

        Message message = Message.builder().message("test message").build();
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    @GetMapping("/error")
    public ResponseEntity<ApiResponse<Message>> errorTest() {

        throw new ApplicationException(ApplicationError.TEST_ERROR);
    }
}
