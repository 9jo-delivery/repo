package com.sparta.delivery.domain.user.controller;

import com.sparta.delivery.domain.user.dto.request.AdminUpdateUserReqDto;
import com.sparta.delivery.domain.user.dto.request.UpdateUserReqDto;
import com.sparta.delivery.domain.user.dto.response.UpdateUserResDto;
import com.sparta.delivery.domain.user.dto.response.UserResDto;
import com.sparta.delivery.domain.user.service.UserService;
import com.sparta.delivery.global.config.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    // 사용자 목록 검색 - 관리자
    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('MASTER') or hasRole('MANAGER')")
    public ResponseEntity<Page<UserResDto>> getUsers(@RequestParam("page") int page,
                                                     @RequestParam("size") int size,
                                                     @RequestParam("sortBy") String sortBy,
                                                     @RequestParam("isAsc") boolean isAsc,
                                                     @RequestParam(value = "keyword", required = false) String keyword
    ) {

        return ResponseEntity.status(HttpStatus.OK).body(userService.getUsers(page - 1, size, sortBy, isAsc, keyword));
    }

    // 사용자 상세 조회 - 관리자
    @GetMapping("/admin/users/{userId}")
    @PreAuthorize("hasRole('MASTER') or hasRole('MANAGER')")
    public ResponseEntity<UserResDto> getUserDetail(@PathVariable Long userId) {

        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserDetail(userId));
    }

    // 사용자 정보 수정 - 본인
    @PatchMapping("/users")
    public ResponseEntity<UpdateUserResDto> updateUser(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                       @Valid @RequestBody UpdateUserReqDto reqDto) {

        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUser(userDetails.getUser().getId(), reqDto));
    }

    // 사용자 정보 수정 - 관리자
    @PatchMapping("/admin/users/{userId}")
    @PreAuthorize("hasRole('MASTER') or hasRole('MANAGER')")
    public ResponseEntity<UpdateUserResDto> adminUpdateUser(@PathVariable Long userId,
                                                            @Valid @RequestBody AdminUpdateUserReqDto reqDto) {

        return ResponseEntity.status(HttpStatus.OK).body(userService.AdminUpdateUser(userId, reqDto));
    }

    // 회원 탈퇴 - 본인
    @DeleteMapping("/users")
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        userService.userIsDelete(userDetails.getUser().getId());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 회원 정보 삭제 - 관리자
    @DeleteMapping("/admin/users/{userId}")
    @PreAuthorize("hasRole('MASTER') or hasRole('MANAGER')")
    public ResponseEntity<Void> AdminDeleteUser(@PathVariable Long userId) {

        userService.userIsDelete(userId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
