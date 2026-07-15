package com.sparta.delivery.domain.menu.controller;

import com.sparta.delivery.domain.menu.dto.MenuOptionCreateRequest;
import com.sparta.delivery.domain.menu.dto.MenuOptionCreateResponse;
import com.sparta.delivery.domain.menu.dto.MenuOptionSearchResponse;
import com.sparta.delivery.domain.menu.dto.MenuOptionUpdateRequest;
import com.sparta.delivery.domain.menu.service.MenuOptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class MenuOptionController {

    public final MenuOptionService menuOptionService;
    private final Long userId = 1L; // 임시 유저 ID

    @PostMapping("/api/option-groups/{groupId}/options")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<MenuOptionCreateResponse> createOption(@PathVariable UUID groupId, @Valid @RequestBody MenuOptionCreateRequest request) {

        MenuOptionCreateResponse response = menuOptionService.createMenuOption(groupId, request, userId);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/api/option-groups/{groupId}/options")
    public ResponseEntity<Page<MenuOptionSearchResponse>> getOptionByGroup(@PathVariable UUID groupId, Pageable pageable) {

        Page<MenuOptionSearchResponse> response = menuOptionService.getOptionByGroup(groupId, pageable);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/api/options/{optionId}")
    public ResponseEntity<MenuOptionSearchResponse> getOption(@PathVariable UUID optionId) {

        MenuOptionSearchResponse response = menuOptionService.getOptionById(optionId);
        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/api/options/{optionId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<MenuOptionSearchResponse> updateOption(@PathVariable UUID optionId, @Valid @RequestBody MenuOptionUpdateRequest request) {

        MenuOptionSearchResponse response = menuOptionService.updateOption(optionId, request, userId);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/api/options/{optionId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> deleteOption(@PathVariable UUID optionId) {

        menuOptionService.deleteOption(optionId, userId);
        return ResponseEntity.noContent().build();
    }
}
