package com.sparta.delivery.domain.restaurant.controller;

import com.sparta.delivery.domain.restaurant.dto.*;
import com.sparta.delivery.domain.restaurant.service.RestaurantCategoryService;
import com.sparta.delivery.global.config.security.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
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

    @PostMapping // TODO: 유저/권한 관련 부분 추후 추가 예정: Manager, Master
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

    @PatchMapping("/{id}") // TODO: 유저/권한 관련 부분 추후 추가 예정: Manager, Master
    public ResponseEntity<CategorySummaryResDto> updateCategory(@PathVariable UUID id, @Valid @RequestBody CategoryUpdateReqDto categoryUpdateReqDto) {
        return ResponseEntity.ok(rcService.updateCategory(id, categoryUpdateReqDto));
    }

    @DeleteMapping("{categoryId}")
    public ResponseEntity<Void> deleteCategory(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable UUID categoryId) {
        Long userId = userDetails.getUser().getId();
        rcService.deleteCategory(userId, categoryId);
        return ResponseEntity.noContent().build();
    }
}
