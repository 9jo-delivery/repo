package com.sparta.delivery.global.config.security.jwt;

import com.sparta.delivery.global.common.Enums;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    // JWT 데이터
    public static final String ACCESS_TOKEN_HEADER = "Authorization";
    public static final String REFRESH_TOKEN_HEADER = "RefreshToken";
    public static final String ACCESS_TOKEN_KEY = "auth";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final long ACCESS_TOKEN_TIME = 60 * 60 * 1000L;
    public static final long REFRESH_TOKEN_TIME = 14 * 24 * 60 * 60 * 1000L;

    private final StringRedisTemplate stringRedisTemplate;

    @Value("${jwt.secret.key}")
    private String secretKey;
    private Key key; // SecretKey를 Key에 담아 암호화 및 복호화로 검증할 때 활용
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // JWT 생성
    public String createAccessToken(String username, Enums.UserRole role) {
        Date date = new Date();

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(username) // 사용자 식별자값
                        .claim(ACCESS_TOKEN_KEY, role) // 사용자 권한
                        .setExpiration(new Date(date.getTime() + ACCESS_TOKEN_TIME)) // 만료 시간
                        .setIssuedAt(date) // 발급일
                        .signWith(key, signatureAlgorithm) // 암호화
                        .compact();
    }

    public String createRefreshToken(String username) {
        Date date = new Date();
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(date.getTime() + REFRESH_TOKEN_TIME))
                .setIssuedAt(date)
                .signWith(key, signatureAlgorithm)
                .compact();
    }

    // 생성된 JWT Cookie에 저장
    public void addAccessTokenToCookie(String token, HttpServletResponse response) {
        try {
            token = URLEncoder.encode(token, "utf-8").replaceAll("\\+", "%20");

            Cookie cookie = new Cookie(ACCESS_TOKEN_HEADER, token);
            cookie.setPath("/");

            // Response 객체에 Cookie 추가
            response.addCookie(cookie);
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage());
        }
    }

    public void addRefreshTokenToCookie(String token, HttpServletResponse response) {
        try {
            token = URLEncoder.encode(token, "utf-8").replaceAll("\\+", "%20");

            Cookie cookie = new Cookie(REFRESH_TOKEN_HEADER, token);
            cookie.setPath("/");
            cookie.setHttpOnly(true);

            // 쿠키 수명을 리프레시 토큰 만료 시간과 동기화
            cookie.setMaxAge((int) (REFRESH_TOKEN_TIME / 1000));

            response.addCookie(cookie);
        } catch (UnsupportedEncodingException e) {
            log.error("리프레시 토큰 쿠키 저장 실패: {}", e.getMessage());
        }
    }

    // Cookie에 들어있던 JWT 토큰을 Substring
    public String substringToken(String tokenValue) {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            return tokenValue.substring(7);
        }
        log.error("토큰을 찾을 수 없습니다.");
        throw new NullPointerException("토큰을 찾을 수 없습니다.");
    }

    // JWT 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            log.error("유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            log.error("만료된  JWT 토큰 입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            log.error("잘못된 JWT 토큰 입니다.");
        }
        return false;
    }

    // JWT에서 사용자 정보 가져오기
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    // 만료된 토큰이더라도 내부 유저 정보를 억지로 꺼내오는 메서드
    public Claims getUserInfoFromExpiredToken(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims(); // 만료되었어도 Claims를 강제로 반환하여 유저 이름을 꺼낼 수 있음
        }
    }

    // HttpServletRequest 에서 Cookie Value : AccessToken 가져오기
    public String getTokenFromRequest(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();

        if (cookies == null) return null;

        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(ACCESS_TOKEN_HEADER))
                .map(cookie -> URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8))
                .findFirst()
                .orElse(null);
    }

    // HttpServletRequest 에서 Cookie Value : RefreshToken 가져오기
    public String getRefreshTokenFromRequest(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();

        if (cookies == null) return null;

        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(REFRESH_TOKEN_HEADER))
                .map(cookie -> URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8))
                .findFirst()
                .orElse(null);
    }

    // Access Token 남은 유효시간만큼 Redis 블랙리스트에 등록
    public void registerBlacklist(String accessToken, long remainTime) {
        stringRedisTemplate.opsForValue().set(
                "BL:" + accessToken,
                "logout",
                remainTime,
                TimeUnit.MILLISECONDS
        );
        log.info("블랙리스트 등록 완료 (TTL: {}ms) - Token: {}", remainTime, accessToken);
    }

    // 해당 토큰이 Redis 블랙리스트(로그아웃 상태)에 존재하는지 검증
    public boolean isBlacklisted(String accessToken) {
        return stringRedisTemplate.opsForValue().get("BL:" + accessToken) != null;
    }

    // 라이언트 브라우저의 AccessToken / RefreshToken 쿠키를 완전히 제거
    public void expireCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 브라우저에게 해당 쿠키 즉시 소멸(삭제) 지시
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }
}
