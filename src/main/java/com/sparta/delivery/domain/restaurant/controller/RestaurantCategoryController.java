package com.sparta.delivery.domain.restaurant.controller;

import com.sparta.delivery.domain.restaurant.dto.*;
import com.sparta.delivery.domain.restaurant.service.RestaurantCategoryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class RestaurantCategoryController {
    public final RestaurantCategoryService rcService;

    public RestaurantCategoryController(RestaurantCategoryService rcService) {
        this.rcService = rcService;
    }

    @PostMapping("/restaurant-categories") // TODO: 유저/권한 관련 부분 추후 추가 예정: Manager, Master
    public ResponseEntity<CategoryCreateResDto> createCategory(@Valid @RequestBody CategoryCreateReqDto categoryCreateReqDto) {
        return ResponseEntity.ok(rcService.createCategory(categoryCreateReqDto));
    }

    @GetMapping("/restaurant-categories")
    public ResponseEntity<Page<CategorySummaryResDto>> getAllCategories(@ModelAttribute CategorySearchReqDto cond) {
        return ResponseEntity.ok(rcService.getAllCategories(cond));
    }

    @GetMapping("/restaurant-categories/{id}")
    public ResponseEntity<CategorySummaryResDto> getCategoryInfo(@PathVariable UUID id) {
       return ResponseEntity.ok(rcService.getCategoryInfo(id));
    }

    @PatchMapping("/restaurant-categories/{id}") // TODO: 유저/권한 관련 부분 추후 추가 예정: Manager, Master
    public ResponseEntity<CategorySummaryResDto> updateCategory(@PathVariable UUID id, @Valid @RequestBody CategoryUpdateReqDto categoryUpdateReqDto) {
        return ResponseEntity.ok(rcService.updateCategory(id, categoryUpdateReqDto));
    }

    @DeleteMapping("/restaurant-categories/{id}") // TODO: 유저/권한 관련 부분 추후 추가 예정: Master
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        rcService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
