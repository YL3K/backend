package com.yl3k.kbsf.user.service;

import com.yl3k.kbsf.jwt.TokenProvider;
import com.yl3k.kbsf.user.dto.TokenDto;
import com.yl3k.kbsf.user.dto.UserRequestDto;
import com.yl3k.kbsf.user.dto.UserResponseDto;
import com.yl3k.kbsf.user.entity.User;
import com.yl3k.kbsf.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InvalidClassException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final AuthenticationManagerBuilder managerBuilder;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    public UserResponseDto signup(UserRequestDto requestDto) {
        if (userRepository.existsByLoginId(requestDto.getLoginId())) {
            throw new RuntimeException("이미 가입되어 있는 유저입니다");
        }

        User user = requestDto.toUser(passwordEncoder);
        return UserResponseDto.of(userRepository.save(user));
    }

    public boolean isLoginIdAvailable(String loginId) {
        return !userRepository.existsByLoginId(loginId); // 아이디가 없으면 true, 있으면 false 반환
    }

    public TokenDto login(UserRequestDto requestDto) {
        UsernamePasswordAuthenticationToken authenticationToken = requestDto.toAuthentication();

        Authentication authentication = managerBuilder.getObject().authenticate(authenticationToken);

        return tokenProvider.generateTokenDto(authentication);
    }

    public User getCurrentUser() {
        // SecurityContext에서 현재 인증된 사용자 정보 가져오기
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // userId로 사용자 정보 조회
        Optional<User> user = userRepository.findByUserId(currentUser.getUserId());

        // 사용자 정보가 없을 경우 예외 처리
        return user.orElseThrow();
    }


}