package com.sparta.delivery.domain.auth.service;

import com.sparta.delivery.domain.auth.dto.request.SignupReqDto;
import com.sparta.delivery.domain.auth.dto.response.SignupResDto;
import com.sparta.delivery.domain.auth.entity.RefreshToken;
import com.sparta.delivery.domain.auth.repository.RefreshTokenRepository;
import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.domain.user.repository.UserRepository;
import com.sparta.delivery.global.common.Enums;
import com.sparta.delivery.global.config.security.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public SignupResDto signup(SignupReqDto reqDto) {

        // 중복 유저 검증
        userRepository.findByUsername(reqDto.getUsername()).ifPresent(checkUser
                -> {throw new IllegalArgumentException("이미 존재하는 유저입니다.");
        });

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(reqDto.getPassword());

        // 삼항 연산자 활용하여 if문으로 반복되는 코드 사용 최적화
        Enums.UserRole role = reqDto.isOwner() ? Enums.UserRole.OWNER : Enums.UserRole.CUSTOMER;

        // 유저 DB에 등록
        User user = userRepository.save(
                User.builder()
                        .username(reqDto.getUsername())
                        .password(encodedPassword)
                        .name(reqDto.getName())
                        .phone(reqDto.getPhone())
                        .role(role)
                        .build());

        return new SignupResDto(user);
    }

    @Transactional
    public void saveRefreshToken(String username, String token) {

        // save() 메서드가 '이미 있으면 갱신, 없으면 신규 저장'을 자동으로 처리
        refreshTokenRepository.save(new RefreshToken(username, token));
    }

    @Transactional
    public void reissue(HttpServletRequest request, HttpServletResponse response) {

        // 1. 요청(쿠키)에서 토큰들 꺼내기
        String expiredAccessTokenValue = jwtUtil.getTokenFromRequest(request);
        String refreshTokenValue = jwtUtil.getRefreshTokenFromRequest(request);

        // 2. 리프레시 토큰 자체의 기본 유효성 검증
        if (refreshTokenValue == null || !jwtUtil.validateToken(refreshTokenValue)) {
            throw new IllegalArgumentException("리프레시 토큰이 만료되었거나 유효하지 않습니다. 다시 로그인해주세요.");
        }

        // 3. 만료된 액세스 토큰에서 안전하게 유저 이름(username) 추출
        if (expiredAccessTokenValue == null) {
            throw new IllegalArgumentException("액세스 토큰이 존재하지 않습니다.");
        }
        String cleanAccessToken = jwtUtil.substringToken(expiredAccessTokenValue);
        Claims claims = jwtUtil.getUserInfoFromExpiredToken(cleanAccessToken);
        String username = claims.getSubject();

        // 4. Redis에 저장된 리프레시 토큰을 가져와 클라이언트가 보낸 것과 똑같은지 비교 (보안 핵심)
        RefreshToken savedRefreshToken = refreshTokenRepository.findById(username)
                .orElseThrow(() -> new IllegalArgumentException("서버에 저장된 인증 정보가 없습니다."));

        if (!savedRefreshToken.getRefreshToken().equals(refreshTokenValue)) {
            throw new IllegalArgumentException("토큰 인증 정보가 일치하지 않습니다. 탈취 위험이 있습니다.");
        }

        // 5. 유저 정보 조회 후 새로운 액세스 토큰 및 리프레시 토큰 재발급
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        String newAccessToken = jwtUtil.createAccessToken(user.getUsername(), user.getRole());
        String newRefreshToken = jwtUtil.createRefreshToken(user.getUsername());

        // 6. DB 갱신 및 클라이언트 쿠키 세팅
        savedRefreshToken.updateRefreshToken(newRefreshToken);
        jwtUtil.addAccessTokenToCookie(newAccessToken, response);
        jwtUtil.addRefreshTokenToCookie(newRefreshToken, response);
    }
}
