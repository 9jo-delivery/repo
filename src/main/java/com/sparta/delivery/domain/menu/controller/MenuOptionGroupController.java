package com.sparta.delivery.domain.menu.controller;

import com.sparta.delivery.domain.menu.dto.MenuOptionGroupCreateRequest;
import com.sparta.delivery.domain.menu.dto.MenuOptionGroupCreateResponse;
import com.sparta.delivery.domain.menu.dto.MenuOptionGroupSearchResponse;
import com.sparta.delivery.domain.menu.dto.MenuOptionGroupUpdateRequest;
import com.sparta.delivery.domain.menu.service.MenuOptionGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
public class MenuOptionGroupController {

    private final MenuOptionGroupService menuOptionGroupService;
    private final Long userId = 1L; // 임시 유저 ID

    // 옵션 그룹 등록
    @PostMapping("/api/menus/{menuId}/option-groups")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<MenuOptionGroupCreateResponse> createOptionGroup(@PathVariable UUID menuId, @Valid @RequestBody MenuOptionGroupCreateRequest request) {

        MenuOptionGroupCreateResponse response = menuOptionGroupService.createMenuOptionGroup(menuId, request, userId);
        return ResponseEntity.created(URI.create("/api/option-groups/" + response.optionGroupId())).body(response);
    }

    // 옵션 그룹 목록 검색
    @GetMapping("/api/menus/{menuId}/option-groups")
    public ResponseEntity<Page<MenuOptionGroupSearchResponse>> getOptionGroupsByMenu(
            @PathVariable UUID menuId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Pageable validatedPageable = validatePageable(pageable);

        Page<MenuOptionGroupSearchResponse> response = menuOptionGroupService.getOptionGroupByMenu(menuId, validatedPageable);
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
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<MenuOptionGroupSearchResponse> updateOptionGroup(@PathVariable UUID groupId, @Valid @RequestBody MenuOptionGroupUpdateRequest request) {

        MenuOptionGroupSearchResponse response = menuOptionGroupService.updateOptionGroup(groupId, request, userId);
        return ResponseEntity.ok(response);
    }

    // 옵션 그룹 삭제
    @DeleteMapping("/api/option-groups/{groupId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> deleteOptionGroup(@PathVariable UUID groupId) {

        menuOptionGroupService.optionGroupDelete(groupId, userId);
        return ResponseEntity.noContent().build();
    }

    private Pageable validatePageable(Pageable pageable) {
        int pageNumber = Math.max(pageable.getPageNumber(), 0);

        int size = pageable.getPageSize();
        if (size != 10 && size != 30 && size != 50) {
            size = 10;
        }

        Sort sort = pageable.getSort().isSorted()
                ? pageable.getSort()
                : Sort.by(Sort.Direction.DESC, "createdAt");

        return PageRequest.of(pageNumber, size, sort);
    }
}
