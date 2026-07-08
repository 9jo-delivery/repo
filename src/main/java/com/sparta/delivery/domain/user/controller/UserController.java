package com.sparta.delivery.domain.user.controller;

import com.sparta.delivery.domain.user.dto.response.GetMyInfoResDto;
import com.sparta.delivery.domain.user.service.UserService;
import com.sparta.delivery.global.config.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // 내 정보 조회
    @GetMapping("/me")
    public ResponseEntity<GetMyInfoResDto> getMyInfoById(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserById(userDetails.getUser().getId()));
    }

    // 사용자 목록 검색

    // 사용자 상세 조회

    // 사용자 정보 수정

    // 회원 탈퇴(삭제)
}
