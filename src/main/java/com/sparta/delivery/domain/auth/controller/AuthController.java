package com.sparta.delivery.domain.auth.controller;

import com.sparta.delivery.domain.auth.dto.request.SignupReqDto;
import com.sparta.delivery.domain.auth.dto.response.SignupResDto;
import com.sparta.delivery.domain.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<SignupResDto> signup(@Valid @RequestBody SignupReqDto reqDto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(authService.signup(reqDto));
    }

    @PostMapping("/reissue")
    public ResponseEntity<String> reissueToken(HttpServletRequest request, HttpServletResponse response) {
        authService.reissue(request, response);
        return ResponseEntity.ok("토큰 재발급이 성공적으로 완료되었습니다.");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        // 서비스 레이어로 요청과 응답 객체를 넘겨 로그아웃 로직 수행
        authService.logout(request, response);

        return ResponseEntity.ok("로그아웃이 성공적으로 완료되었습니다.");
    }
}
