package com.yl3k.kbsf.jwt;

import com.yl3k.kbsf.user.dto.TokenDto;
import com.yl3k.kbsf.user.entity.User;
import com.yl3k.kbsf.user.entity.UserType;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class TokenProvider {

    private static final Logger log = LoggerFactory.getLogger(TokenProvider.class); // Logger 객체 선언


    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_TYPE = "bearer";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30;
    private final SecretKey secretKey;



    // 주의점: 여기서 @Value는 `springframework.beans.factory.annotation.Value`소속이다! lombok의 @Value와 착각하지 말것!
    //     * @param secretKey
    public TokenProvider(@Value("${jwt.secretKey}") String secretKey) {
        // secretKey가 512비트 이상인지를 체크하고, 그렇지 않으면 안전한 비밀 키 생성
        if (secretKey.length() < 64) {
            // 비밀 키 길이가 512비트보다 작다면, JJWT 라이브러리의 Keys.secretKeyFor()로 안전한 비밀 키 생성
            this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512); // 최소 512비트 키 생성
            log.warn("Provided secretKey is too short, using generated key for HS512.");
        } else {
            // 충분히 긴 비밀 키가 제공되면, 기존 키 사용
            this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        }
    }


    // 토큰 생성
    public TokenDto generateTokenDto(Authentication authentication) {

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date tokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        System.out.println(tokenExpiresIn);

        // UserDetails에서 사용자 정보를 가져옵니다.
        User principal = (User) authentication.getPrincipal();
        Integer userId = principal.getUserId(); // User 엔티티에서 loginId
        String userType = principal.getUserType().name(); // User 엔티티에서 userType


        // 수정된 부분: SignatureAlgorithm과 Key를 정확하게 전달
        String accessToken = Jwts.builder()
                .setSubject(userId.toString())
                .claim(AUTHORITIES_KEY, authorities) // 권한 정보 추가
                .claim("userType", userType) // userType 추가
                .setExpiration(tokenExpiresIn)
                .signWith(SignatureAlgorithm.HS512, secretKey)  // Key 객체와 SignatureAlgorithm을 정확히 사용
                .compact();

        return TokenDto.builder()
                .userId(userId)
                .userType(userType)
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .tokenExpiresIn(tokenExpiresIn.getTime())
                .build();
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // JWT에서 userType 추출
        String userTypeStr = claims.get("userType", String.class);
        UserType userType = UserType.valueOf(userTypeStr);

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // 추출된 권한을 기반으로 UserType을 결정합니다.
//        UserType userType = UserType.valueOf(claims.get(AUTHORITIES_KEY).toString().split(",")[0]);

        // User 객체 생성 - loginId (claims.getSubject())와 userType 사용
        User principal = new User(claims.getSubject(), "", userType);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }


    public boolean validateToken(String token) throws IOException {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}