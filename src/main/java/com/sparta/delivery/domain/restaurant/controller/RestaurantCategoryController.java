package com.sparta.delivery.domain.restaurant.controller;

import com.sparta.delivery.domain.restaurant.dto.CategorySummaryResDto;
import com.sparta.delivery.domain.restaurant.dto.CategorySearchReqDto;
import com.sparta.delivery.domain.restaurant.dto.CategoryReqDto;
import com.sparta.delivery.domain.restaurant.dto.CategoryCreateResDto;
import com.sparta.delivery.domain.restaurant.service.RestaurantCategoryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RestaurantCategoryController {
    public final RestaurantCategoryService rcService;

    public RestaurantCategoryController(RestaurantCategoryService rcService) {
        this.rcService = rcService;
    }

    @PostMapping("/restaurant-categories")
    public ResponseEntity<CategoryCreateResDto> createCategory(@Valid @RequestBody CategoryReqDto categoryReqDto) {
        return ResponseEntity.ok(rcService.createCategory(categoryReqDto));
    }

    @GetMapping("/restaurant-categories")
    public ResponseEntity<Page<CategorySummaryResDto>> getAllCategories(@ModelAttribute CategorySearchReqDto cond) {
        return ResponseEntity.ok(rcService.getAllCategories(cond));
    }
}
