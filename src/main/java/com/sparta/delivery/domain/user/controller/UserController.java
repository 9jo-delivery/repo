package com.sparta.delivery.domain.user.controller;

import com.sparta.delivery.domain.user.dto.request.UpdateUserReqDto;
import com.sparta.delivery.domain.user.dto.response.UpdateUserResDto;
import com.sparta.delivery.domain.user.dto.response.UserResDto;
import com.sparta.delivery.domain.user.service.UserService;
import com.sparta.delivery.global.common.Enums;
import com.sparta.delivery.global.config.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    // 내 정보 조회
    @GetMapping("/users/me")
    public ResponseEntity<UserResDto> getMyInfoById(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserById(userDetails.getUser().getId()));
    }

    // 사용자 목록 검색
    @GetMapping("/admin/users")
    @Secured({Enums.UserRole.Authority.MASTER, Enums.UserRole.Authority.MANAGER})
    public ResponseEntity<Page<UserResDto>> getUsers(@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.status(HttpStatus.OK).body(userService.getUsers(pageable));
    }

    // 사용자 상세 조회
    @GetMapping("/admin/users/{userId}")
    @Secured({Enums.UserRole.Authority.MASTER, Enums.UserRole.Authority.MANAGER})
    public ResponseEntity<UserResDto> getUserDetail(@PathVariable Long userId) {

        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserDetail(userId));
    }

    // 사용자 정보 수정 - 자기자신
    @PatchMapping("/users")
    public ResponseEntity<UpdateUserResDto> updateUser(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                       @Valid @RequestBody UpdateUserReqDto reqDto) {

        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUser(userDetails.getUser().getId(), reqDto));
    }

    // 회원 탈퇴(삭제)
}
