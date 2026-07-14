package com.sparta.delivery.domain.restaurant.controller;

import com.sparta.delivery.domain.restaurant.dto.*;
import com.sparta.delivery.domain.restaurant.service.RestaurantService;
import com.sparta.delivery.global.config.security.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {
    public final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    // 가게 등록
    @PreAuthorize("hasRole('OWNER')")
    @PostMapping
    public ResponseEntity<RestaurantCreateResDto> createRestaurant(@AuthenticationPrincipal UserDetailsImpl userDetails, @Valid @RequestBody RestaurantCreateReqDto restaurantCreateReqDto) {
        Long userId = userDetails.getUser().getId();

        return ResponseEntity.ok(restaurantService.createRestaurant(userId, restaurantCreateReqDto));
    }

    // 가게 목록 검색
    @GetMapping
    public ResponseEntity<Page<RestaurantSummaryResDto>> getAllRestaurants(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                           @PageableDefault(page=0, size=10, sort="createdAt", direction=Sort.Direction.DESC) Pageable pageable,
                                                                           @ModelAttribute RestaurantSearchReqDto restaurantSearchReqDto) {
        Long userId = userDetails.getUser().getId();
        return ResponseEntity.ok(restaurantService.getAllRestaurants(userId, pageable, restaurantSearchReqDto));
    }

    // 가게 상세 조회
    @GetMapping("/{restaurantId}")
    public ResponseEntity<RestaurantSummaryResDto> getRestaurantInfo(@PathVariable UUID restaurantId) {
        return ResponseEntity.ok(restaurantService.getRestaurantInfo(restaurantId));
    }

    // 가게 정보 수정
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
    @PatchMapping("/{restaurantId}")
    public ResponseEntity<RestaurantSummaryResDto> updateRestaurant(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                    @PathVariable UUID restaurantId,
                                                                    @Valid @RequestBody RestaurantUpdateReqDto restaurantUpdateReqDto) {
        Long userId = userDetails.getUser().getId();
        return ResponseEntity.ok(restaurantService.updateRestaurant(userId, restaurantId, restaurantUpdateReqDto));
    }

    // 가게 정보 삭제
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
    @DeleteMapping("/{restaurantId}")
    public ResponseEntity<Void> deleteRestaurant(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable UUID restaurantId) {
        Long userId = userDetails.getUser().getId();
        restaurantService.deleteRestaurant(userId, restaurantId);
        return ResponseEntity.noContent().build();
    }
}
