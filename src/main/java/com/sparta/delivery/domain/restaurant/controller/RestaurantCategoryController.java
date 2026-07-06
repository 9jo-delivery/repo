package com.sparta.delivery.domain.restaurant.controller;

import com.sparta.delivery.domain.restaurant.dto.RCGetResponseDto;
import com.sparta.delivery.domain.restaurant.dto.RCRequestDto;
import com.sparta.delivery.domain.restaurant.dto.RCCreateResponseDto;
import com.sparta.delivery.domain.restaurant.service.RestaurantCategoryService;
import jakarta.validation.Valid;
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
    public ResponseEntity<RCCreateResponseDto> createCategory(@Valid @RequestBody RCRequestDto rcRequestDto) {
        return ResponseEntity.ok(rcService.createCategory(rcRequestDto));
    }


}
