package com.sparta.delivery.domain.restaurant.controller;

import com.sparta.delivery.domain.restaurant.dto.RestaurantCreateReqDto;
import com.sparta.delivery.domain.restaurant.dto.RestaurantCreateResDto;
import com.sparta.delivery.domain.restaurant.dto.RestaurantSearchReqDto;
import com.sparta.delivery.domain.restaurant.dto.RestaurantSummaryResDto;
import com.sparta.delivery.domain.restaurant.service.RestaurantService;
import com.sparta.delivery.global.config.security.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {
    public final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    // 가게 등록
    @PostMapping
    public ResponseEntity<RestaurantCreateResDto> createRestaurant(@AuthenticationPrincipal UserDetailsImpl userDetails, @Valid @RequestBody RestaurantCreateReqDto restaurantCreateReqDto) {
        // TODO: 유저 세팅용 하드코딩 수정
        // Long userId = userDetails.getUser().getId();
        Long userId = 1L;

        return ResponseEntity.ok(restaurantService.createRestaurant(userId, restaurantCreateReqDto));
    }

    // 가게 목록 검색
    @GetMapping
    public ResponseEntity<Page<RestaurantSummaryResDto>> getAllRestaurants(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                           @PageableDefault(page=0, size=10, sort="createdAt", direction=Sort.Direction.DESC) Pageable pageable,
                                                                           @ModelAttribute RestaurantSearchReqDto restaurantSearchReqDto) {
        // TODO: 유저 세팅용 하드코딩 수정
        // Long userId = userDetails.getUser().getId();
        Long userId = 1L;
        return ResponseEntity.ok(restaurantService.getAllRestaurants(userId, pageable, restaurantSearchReqDto));
    }

}
