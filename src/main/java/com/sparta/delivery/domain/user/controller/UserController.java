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
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // 내 정보 조회
    @GetMapping("/me")
    public ResponseEntity<UserResDto> getMyInfoById(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserById(userDetails.getUser().getId()));
    }

    // 사용자 목록 검색
    @GetMapping
    @Secured({Enums.UserRole.Authority.MASTER, Enums.UserRole.Authority.MANAGER})
    public ResponseEntity<Page<UserResDto>> getUsers(@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.status(HttpStatus.OK).body(userService.getUsers(pageable));
    }

    // 사용자 상세 조회
    @GetMapping("/{userId}")
    @Secured({Enums.UserRole.Authority.MASTER, Enums.UserRole.Authority.MANAGER})
    public ResponseEntity<UserResDto> getUserDetail(@PathVariable Long userId) {

        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserDetail(userId));
    }

    // 사용자 정보 수정 - 자기자신
    @PatchMapping("/{userId}")
    public ResponseEntity<UpdateUserResDto> updateUser(@PathVariable Long userId,
                                                       @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                       @Valid @RequestBody UpdateUserReqDto reqDto) {

        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUser(userId, userDetails.getUser(), reqDto));
    }

    // 회원 탈퇴(삭제)
}
