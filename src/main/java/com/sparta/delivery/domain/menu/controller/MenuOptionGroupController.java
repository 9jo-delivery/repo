package com.sparta.delivery.domain.menu.controller;

import com.sparta.delivery.domain.menu.dto.MenuOptionGroupCreateRequest;
import com.sparta.delivery.domain.menu.dto.MenuOptionGroupCreateResponse;
import com.sparta.delivery.domain.menu.dto.MenuOptionGroupSearchResponse;
import com.sparta.delivery.domain.menu.dto.MenuOptionGroupUpdateRequest;
import com.sparta.delivery.domain.menu.service.MenuOptionGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class MenuOptionGroupController {

    private final MenuOptionGroupService menuOptionGroupService;
    private final Long userId = 1L; // 임시 유저 ID

    // 옵션 그룹 등록
    @PostMapping("/api/menus/{menuId}/option-groups")
    public ResponseEntity<MenuOptionGroupCreateResponse> createOptionGroup(@PathVariable UUID menuId, @Valid @RequestBody MenuOptionGroupCreateRequest request) {

        MenuOptionGroupCreateResponse response = menuOptionGroupService.createMenuOptionGroup(menuId, request, userId);
        return ResponseEntity.created(URI.create("/api/option-groups/" + response.optionGroupId())).body(response);
    }

    // 옵션 그룹 목록 검색
    @GetMapping("/api/menus/{menuId}/option-groups")
    public ResponseEntity<Page<MenuOptionGroupSearchResponse>> getOptionGroupsByMenu(@PathVariable UUID menuId, Pageable pageable) {

        Page<MenuOptionGroupSearchResponse> response = menuOptionGroupService.getOptionGroupByMenu(menuId, pageable);
        return ResponseEntity.ok(response);
    }

    // 옵션 그룹 상세 조회
    @GetMapping("/api/option-groups/{groupId}")
    public ResponseEntity<MenuOptionGroupSearchResponse> getOptionGroup(@PathVariable UUID groupId) {

        MenuOptionGroupSearchResponse response = menuOptionGroupService.getOptionGroupDetails(groupId);
        return ResponseEntity.ok(response);
    }

    // 옵션 그룹 수정
    @PatchMapping("/api/option-groups/{groupId}")
    public ResponseEntity<MenuOptionGroupSearchResponse> updateOptionGroup(@PathVariable UUID groupId, @Valid @RequestBody MenuOptionGroupUpdateRequest request) {

        MenuOptionGroupSearchResponse response = menuOptionGroupService.updateOptionGroup(groupId, request, userId);
        return ResponseEntity.ok(response);
    }

    // 옵션 그룹 삭제
    @DeleteMapping("/api/option-groups/{groupId}")
    public ResponseEntity<Void> deleteOptionGroup(@PathVariable UUID groupId) {

        menuOptionGroupService.optionGroupDelete(groupId, userId);
        return ResponseEntity.noContent().build();
    }
}
