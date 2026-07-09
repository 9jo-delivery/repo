package com.sparta.delivery.domain.restaurant.controller;

import com.sparta.delivery.domain.restaurant.dto.RestaurantCreateReqDto;
import com.sparta.delivery.domain.restaurant.dto.RestaurantCreateResDto;
import com.sparta.delivery.domain.restaurant.service.RestaurantService;
import com.sparta.delivery.global.config.security.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RestaurantController {
    public final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    // 가게 등록
    @PostMapping("/restaurants")
    public ResponseEntity<RestaurantCreateResDto> createRestaurant(@AuthenticationPrincipal UserDetailsImpl userDetails, @Valid @RequestBody RestaurantCreateReqDto restaurantCreateReqDto) {
        // TODO: 유저 세팅용 하드코딩 수정
        // Long userId = userDetails.getUser().getId();
        Long userId = 1L;

        return ResponseEntity.ok(restaurantService.createRestaurant(userId, restaurantCreateReqDto));
    }

}
