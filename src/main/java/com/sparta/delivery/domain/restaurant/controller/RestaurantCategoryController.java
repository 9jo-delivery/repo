package com.sparta.delivery.domain.restaurant.controller;

import com.sparta.delivery.domain.restaurant.dto.request.CategoryCreateReqDto;
import com.sparta.delivery.domain.restaurant.dto.request.CategorySearchReqDto;
import com.sparta.delivery.domain.restaurant.dto.request.CategoryUpdateReqDto;
import com.sparta.delivery.domain.restaurant.dto.response.CategoryCreateResDto;
import com.sparta.delivery.domain.restaurant.dto.response.CategorySummaryResDto;
import com.sparta.delivery.domain.restaurant.service.RestaurantCategoryService;
import com.sparta.delivery.global.config.security.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/restaurant-categories")
public class RestaurantCategoryController {
    public final RestaurantCategoryService rcService;

    public RestaurantCategoryController(RestaurantCategoryService rcService) {
        this.rcService = rcService;
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'MASTER')")
    @PostMapping
    public ResponseEntity<CategoryCreateResDto> createCategory(@Valid @RequestBody CategoryCreateReqDto categoryCreateReqDto) {
        return ResponseEntity.ok(rcService.createCategory(categoryCreateReqDto));
    }

    @GetMapping
    public ResponseEntity<Page<CategorySummaryResDto>> getAllCategories(@ModelAttribute CategorySearchReqDto cond) {
        return ResponseEntity.ok(rcService.getAllCategories(cond));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategorySummaryResDto> getCategoryInfo(@PathVariable UUID id) {
       return ResponseEntity.ok(rcService.getCategoryInfo(id));
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'MASTER')")
    @PatchMapping("/{id}")
    public ResponseEntity<CategorySummaryResDto> updateCategory(@PathVariable UUID id, @Valid @RequestBody CategoryUpdateReqDto categoryUpdateReqDto) {
        return ResponseEntity.ok(rcService.updateCategory(id, categoryUpdateReqDto));
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'MASTER')")
    @DeleteMapping("{categoryId}")
    public ResponseEntity<Void> deleteCategory(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable UUID categoryId) {
        Long userId = userDetails.getUser().getId();
        rcService.deleteCategory(userId, categoryId);
        return ResponseEntity.noContent().build();
    }
}
