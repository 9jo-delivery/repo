package com.sparta.delivery.domain.menu.controller;

import com.sparta.delivery.domain.menu.dto.MenuCreateRequest;
import com.sparta.delivery.domain.menu.dto.MenuCreateResponse;
import com.sparta.delivery.domain.menu.dto.MenuSearchResponse;
import com.sparta.delivery.domain.menu.dto.MenuUpdateRequest;
import com.sparta.delivery.domain.menu.service.MenuService;
import com.sparta.delivery.global.config.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    // 메뉴 등록
    @PostMapping("/api/restaurants/{restaurantId}/menus")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<MenuCreateResponse> createMenu(
            @PathVariable UUID restaurantId,
            @Valid @RequestBody MenuCreateRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long userId = userDetails.getUser().getId();

        MenuCreateResponse response = menuService.createMenu(restaurantId, request, userId);
        // 생성된 메뉴의 상세 조회 URI를 Location 헤더에 담아 201 Created 응답
        return ResponseEntity.created(URI.create("/api/menus/" + response.menuId())).body(response);
    }

    // 메뉴 목록 검색
    @GetMapping("/api/restaurants/{restaurantId}/menus")
    public ResponseEntity<Page<MenuSearchResponse>> getMenusByRestaurant(
            @PathVariable UUID restaurantId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        // 컨트롤러 레벨에서 페이지 음수 방어 및 사이즈 제한 처리
        Pageable validatedPageable = validatePageable(pageable);

        Page<MenuSearchResponse> response = menuService.getMenusByRestaurant(restaurantId, validatedPageable);
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
    public ResponseEntity<MenuSearchResponse> updateMenu(
            @PathVariable UUID menuId,
            @Valid @RequestBody MenuUpdateRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long userId = userDetails.getUser().getId();

        MenuSearchResponse response = menuService.updateMenu(menuId, request, userId);
        return ResponseEntity.ok(response);
    }

    // 메뉴 삭제
    @DeleteMapping("/api/menus/{menuId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> deleteMenu(
            @PathVariable UUID menuId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long userId = userDetails.getUser().getId();

        menuService.deleteMenu(menuId, userId);
        return ResponseEntity.noContent().build();
    }

    // TODO : 글로벌로 빼내기
    // 컨트롤러 레벨에서 페이지 음수 방어 및 사이즈 제한 처리
    private Pageable validatePageable(Pageable pageable) {
        // 음수 페이지 번호 방어 (0보다 작으면 0으로 고정)
        int pageNumber = Math.max(pageable.getPageNumber(), 0);

        // 허용되지 않은 사이즈 방어 (10, 30, 50 이외에는 10으로 고정)
        int size = pageable.getPageSize();
        if (size != 10 && size != 30 && size != 50) {
            size = 10;
        }

        // 정렬 조건 방어: 정렬 값이 없거나 정렬되지 않은 상태라면 최신순(createdAt, DESC)을 기본값으로 지정
        Sort sort = pageable.getSort().isSorted()
                ? pageable.getSort()
                : Sort.by(Sort.Direction.DESC, "createdAt");

        // 검증이 완료된 값들과 원본 정렬 방식을 묶어 PageRequest 새로 생성
        return PageRequest.of(pageNumber, size, sort);
    }
}
