package com.sparta.delivery.domain.menu.controller;

import com.sparta.delivery.domain.menu.dto.MenuOptionCreateRequest;
import com.sparta.delivery.domain.menu.dto.MenuOptionCreateResponse;
import com.sparta.delivery.domain.menu.dto.MenuOptionSearchResponse;
import com.sparta.delivery.domain.menu.dto.MenuOptionUpdateRequest;
import com.sparta.delivery.domain.menu.service.MenuOptionService;
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
public class MenuOptionController {

    private final MenuOptionService menuOptionService;

    @PostMapping("/api/option-groups/{groupId}/options")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<MenuOptionCreateResponse> createOption(
            @PathVariable UUID groupId,
            @Valid @RequestBody MenuOptionCreateRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long userId = userDetails.getUser().getId();

        MenuOptionCreateResponse response = menuOptionService.createMenuOption(groupId, request, userId);
        return ResponseEntity.created(URI.create("/api/options/" + response.optionId())).body(response);
    }

    @GetMapping("/api/option-groups/{groupId}/options")
    public ResponseEntity<Page<MenuOptionSearchResponse>> getOptionByGroup(
            @PathVariable UUID groupId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Pageable validatedPageable = validatePageable(pageable);

        Page<MenuOptionSearchResponse> response = menuOptionService.getOptionByGroup(groupId, validatedPageable);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/api/options/{optionId}")
    public ResponseEntity<MenuOptionSearchResponse> getOption(@PathVariable UUID optionId) {

        MenuOptionSearchResponse response = menuOptionService.getOptionById(optionId);
        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/api/options/{optionId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<MenuOptionSearchResponse> updateOption(
            @PathVariable UUID optionId,
            @Valid @RequestBody MenuOptionUpdateRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long userId = userDetails.getUser().getId();

        MenuOptionSearchResponse response = menuOptionService.updateOption(optionId, request, userId);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/api/options/{optionId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> deleteOption(
            @PathVariable UUID optionId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long userId = userDetails.getUser().getId();

        menuOptionService.deleteOption(optionId, userId);
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
