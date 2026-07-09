package com.sparta.delivery.domain.menu.controller;

import com.sparta.delivery.domain.menu.dto.MenuCreateRequest;
import com.sparta.delivery.domain.menu.dto.MenuResponse;
import com.sparta.delivery.domain.menu.dto.MenuUpdateRequest;
import com.sparta.delivery.domain.menu.service.MenuService;
import com.sparta.delivery.global.common.Enums;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;
    private final Long userId = 1L; // 임시 유저 ID

    // 메뉴 등록
    @PostMapping("/api/restaurants/{restaurantId}/menus")
    public ResponseEntity<Void> createMenu(@PathVariable UUID restaurantId, @Valid @RequestBody MenuCreateRequest request) {
        validationOwnerRole(); // 권한 검증

        UUID createdMenuId = menuService.createMenu(restaurantId, request, userId);
        // 생성된 메뉴의 상세 조회 URI를 Location 헤더에 담아 201 Created 응답
        return ResponseEntity.created(URI.create("/api/menus/" + createdMenuId)).build();
    }

    // 메뉴 목록 검색
    @GetMapping("/api/restaurants/{restaurantId}/menus")
    public ResponseEntity<Page<MenuResponse>> getMenusByRestaurant(@PathVariable UUID restaurantId, Pageable pageable) {
        Page<MenuResponse> response = menuService.getMenusByRestaurant(restaurantId, pageable);
        return ResponseEntity.ok(response);
    }

    // 메뉴 상세 조회
    @GetMapping("/api/menus/{menuId}")
    public ResponseEntity<MenuResponse> getMenuById(@PathVariable UUID menuId) {
        MenuResponse response = menuService.getMenuDetails(menuId);
        return ResponseEntity.ok(response);
    }

    // 메뉴 수정
    @PatchMapping("/api/menus/{menuId}")
    public ResponseEntity<MenuResponse> updateMenu(@PathVariable UUID menuId, @Valid @RequestBody MenuUpdateRequest request) {
        validationOwnerRole(); // 권한 검증

        MenuResponse response = menuService.updateMenu(menuId, request, userId);
        return ResponseEntity.ok(response);
    }

    // 메뉴 삭제
    @DeleteMapping("/api/menus/{menuId}")
    public ResponseEntity<Void> deleteMenu(@PathVariable UUID menuId) {
        validationOwnerRole(); // OWNER 권한 검증

        menuService.deleteMenu(menuId, userId);
        return ResponseEntity.noContent().build();
    }

    // 권한 검증 메서드: 임시 구현
    private void validationOwnerRole() {
        Enums.UserRole userRole = Enums.UserRole.OWNER;
        if (userRole != Enums.UserRole.OWNER) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.FORBIDDEN, "OWNER 권한이 필요합니다."
            );
        }
    }
}
