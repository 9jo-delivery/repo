package com.sparta.delivery.domain.restaurant.service;

import com.sparta.delivery.domain.region.entity.Region;
import com.sparta.delivery.domain.region.repository.RegionRepository;
import com.sparta.delivery.domain.restaurant.dto.RestaurantCreateReqDto;
import com.sparta.delivery.domain.restaurant.dto.RestaurantCreateResDto;
import com.sparta.delivery.domain.restaurant.entity.Restaurant;
import com.sparta.delivery.domain.restaurant.entity.RestaurantCategory;
import com.sparta.delivery.domain.restaurant.repository.RestaurantCategoryRepository;
import com.sparta.delivery.domain.restaurant.repository.RestaurantRepository;
import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.domain.user.repository.UserRepository;
import com.sparta.delivery.global.common.Enums;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final RegionRepository regionRepository;
    private final RestaurantCategoryRepository categoryRepository;

    public RestaurantService(RestaurantRepository restaurantRepository, UserRepository userRepository, RegionRepository regionRepository, RestaurantCategoryRepository categoryRepository) {
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
        this.regionRepository = regionRepository;
        this.categoryRepository = categoryRepository;
    }


    @Transactional
    public RestaurantCreateResDto createRestaurant(Long userId, RestaurantCreateReqDto restaurantCreateReqDto) {
        // 현재 로그인 유저 정보 조회해서 권한 Owner인지 확인
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        if (owner.getRole() != Enums.UserRole.OWNER)
            throw new IllegalArgumentException("가게를 생성하려면 가게 주인이어야 합니다.");

        // dto 내용 검증
            // req - region 찾고, 서비스 가능 지역인지 확인 (가게는 서비스 가능 지역(regions.isServiceAvailable=true)에만 등록 가능)
        Region region = regionRepository.findById(restaurantCreateReqDto.getRegionId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지역입니다."));
        if (!region.isServiceAvailable())
            throw new IllegalArgumentException("서비스 가능 지역에만 가게를 등록할 수 있습니다.");

            // req - category 찾고, 활성화 상태인지 확인 isActive
        RestaurantCategory category = categoryRepository.findById(restaurantCreateReqDto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));
        if (!category.getIsActive())
            throw new IllegalArgumentException("음식점 카테고리가 활성화 상태여야 합니다.");

        // 정보 바탕으로 가게를 생성
        Restaurant newRestaurant = Restaurant.create(
                owner, category, region,
                restaurantCreateReqDto.getName(),
                restaurantCreateReqDto.getDescription(),
                restaurantCreateReqDto.getPhone(),
                restaurantCreateReqDto.getAddress(),
                restaurantCreateReqDto.getDetailAddress(),
                restaurantCreateReqDto.getBusinessNumber(),
                restaurantCreateReqDto.getMinOrderAmount(),
                restaurantCreateReqDto.getDeliveryFee()
        );

        Restaurant savedRestaurant = restaurantRepository.save(newRestaurant);
        // 가게 정보 반환
        return new RestaurantCreateResDto(savedRestaurant);
    }
}
