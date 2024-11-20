package com.yl3k.kbsf.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "user")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(length = 10)
    @Enumerated(EnumType.STRING)
    private UserType userType;

    @CreationTimestamp
    private LocalDateTime createdAt = LocalDateTime.now();


    @Transient // JPA가 관리하지 않도록 함
    private Collection<? extends GrantedAuthority> authorities;

    // UserDetails 생성자 추가
    @Builder
    public User(Integer userId, String password, UserType userType) {
        this.userId = userId;
        this.password = password;
        this.userType = userType;
        this.authorities = getDefaultAuthorities();
    }

    // userType에 따른 기본 권한 설정
    private Collection<? extends GrantedAuthority> getDefaultAuthorities() {
        if (this.userType != null) {
            return List.of(new SimpleGrantedAuthority(this.userType.name()));
        }
        return Collections.emptyList();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // UserType에 따른 권한 부여 또는 필요한 권한을 반환
        return Arrays.stream(userType.toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return this.userName; // 로그인 ID를 username으로 사용
    }
}
