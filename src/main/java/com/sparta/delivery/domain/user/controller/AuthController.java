package com.sparta.delivery.domain.user.controller;

import com.sparta.delivery.domain.user.dto.request.SignupReqDto;
import com.sparta.delivery.domain.user.service.AuthService;
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
    public ResponseEntity<Void> signup(@Valid @RequestBody SignupReqDto reqDto) {
        authService.signup(reqDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // 로그인

    // 로그아웃
}
