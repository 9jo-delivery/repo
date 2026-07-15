package com.sparta.delivery.domain.menu.controller;

import com.sparta.delivery.domain.menu.dto.MenuCreateRequest;
import com.sparta.delivery.domain.menu.dto.MenuCreateResponse;
import com.sparta.delivery.domain.menu.dto.MenuSearchResponse;
import com.sparta.delivery.domain.menu.dto.MenuUpdateRequest;
import com.sparta.delivery.domain.menu.service.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<MenuCreateResponse> createMenu(@PathVariable UUID restaurantId, @Valid @RequestBody MenuCreateRequest request) {

        MenuCreateResponse response = menuService.createMenu(restaurantId, request, userId);
        // 생성된 메뉴의 상세 조회 URI를 Location 헤더에 담아 201 Created 응답
        return ResponseEntity.created(URI.create("/api/menus/" + response.menuId())).body(response);
    }

    // 메뉴 목록 검색
    @GetMapping("/api/restaurants/{restaurantId}/menus")
    public ResponseEntity<Page<MenuSearchResponse>> getMenusByRestaurant(@PathVariable UUID restaurantId, Pageable pageable) {
        Page<MenuSearchResponse> response = menuService.getMenusByRestaurant(restaurantId, pageable);
        return ResponseEntity.ok(response);
    }

    // 메뉴 상세 조회
    @GetMapping("/api/menus/{menuId}")
    public ResponseEntity<MenuSearchResponse> getMenuById(@PathVariable UUID menuId) {
        MenuSearchResponse response = menuService.getMenuDetails(menuId);
        return ResponseEntity.ok(response);
    }

    // 메뉴 수정
    @PatchMapping("/api/menus/{menuId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<MenuSearchResponse> updateMenu(@PathVariable UUID menuId, @Valid @RequestBody MenuUpdateRequest request) {

        MenuSearchResponse response = menuService.updateMenu(menuId, request, userId);
        return ResponseEntity.ok(response);
    }

    // 메뉴 삭제
    @DeleteMapping("/api/menus/{menuId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> deleteMenu(@PathVariable UUID menuId) {

        menuService.deleteMenu(menuId, userId);
        return ResponseEntity.noContent().build();
    }
}
