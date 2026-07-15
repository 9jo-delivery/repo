package com.sparta.delivery.domain.user.controller;

import com.sparta.delivery.domain.user.dto.request.UpdateUserReqDto;
import com.sparta.delivery.domain.user.dto.response.UpdateUserResDto;
import com.sparta.delivery.domain.user.dto.response.UserResDto;
import com.sparta.delivery.domain.user.service.UserService;
import com.sparta.delivery.global.config.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    // @AuthenticationPrincipal 활용하여 내 정보 조회
    @GetMapping("/users/me")
    public ResponseEntity<UserResDto> getMyInfoById(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserById(userDetails.getUser().getId()));
    }

    // 사용자 목록 검색 - 관리자
    @GetMapping("/users")
    @PreAuthorize("hasAnyRole('MASTER', 'MANAGER')")
    public ResponseEntity<Page<UserResDto>> getUsers(@RequestParam("page") int page,
                                                     @RequestParam("size") int size,
                                                     @RequestParam("sortBy") String sortBy,
                                                     @RequestParam("isAsc") boolean isAsc,
                                                     @RequestParam(value = "keyword", required = false) String keyword) {
        // 페이징 처리
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        return ResponseEntity.status(HttpStatus.OK).body(userService.getUsers(keyword, pageable));
    }

    // 사용자 상세 조회 - 본인이거나 관리자
    @GetMapping("/users/{userId}")
    @PreAuthorize("#userId == principal.user.id or hasAnyRole('MASTER', 'MANAGER')")
    public ResponseEntity<UserResDto> getUserDetail(@PathVariable("userId") Long userId) {

        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserDetail(userId));
    }

    // 회원 정보 수정 - 본인이거나 관리자
    @PatchMapping("/users/{userId}")
    @PreAuthorize("#userId == principal.user.id or hasAnyRole('MASTER', 'MANAGER')")
    public ResponseEntity<UpdateUserResDto> UpdateUser(@PathVariable Long userId,
                                                       @AuthenticationPrincipal UserDetailsImpl loginUser,
                                                       @Valid @RequestBody UpdateUserReqDto reqDto) {

        return ResponseEntity.status(HttpStatus.OK).body(userService.UpdateUser(userId, loginUser.getUser(), reqDto));
    }

    // 회원 정보 삭제 - 본인이거나 관리자
    @DeleteMapping("/users/{userId}")
    @PreAuthorize("#userId == principal.user.id or hasAnyRole('MASTER', 'MANAGER')")
    public ResponseEntity<Void> AdminDeleteUser(@PathVariable Long userId) {

        userService.userIsDelete(userId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
