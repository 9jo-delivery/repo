package com.sparta.delivery.domain.restaurant.service;

import com.sparta.delivery.domain.restaurant.dto.RCRequestDto;
import com.sparta.delivery.domain.restaurant.dto.RCCreateResponseDto;
import com.sparta.delivery.domain.restaurant.entity.RestaurantCategory;
import com.sparta.delivery.domain.restaurant.repository.RestaurantCategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class RestaurantCategoryService {
    private final RestaurantCategoryRepository rcRepository;

    public RestaurantCategoryService(RestaurantCategoryRepository rcRepository) {
        this.rcRepository = rcRepository;
    }

    @Transactional
    public RCCreateResponseDto createCategory(RCRequestDto rcRequestDto) { //TODO: 권한 관련 부분 추후 추가 예정
        // 중복 카테고리 여부 검증
        Optional<RestaurantCategory> category = rcRepository.findByName(rcRequestDto.getName());
        if (category.isPresent()) {
            throw new IllegalArgumentException("이미 등록되어 있는 카테고리입니다.");
        }

        // 값 체크
        Integer sortOrder = rcRequestDto.getSortOrder() == null ? 0 : rcRequestDto.getSortOrder();

        // 객체 생성
        RestaurantCategory restaurantCategory = RestaurantCategory.builder()
                .name(rcRequestDto.getName())
                .description(rcRequestDto.getDescription())
                .sortOrder(sortOrder)
                .isActive(true)
                .build();

        RestaurantCategory savedCategory = rcRepository.save(restaurantCategory);
        return new RCCreateResponseDto(savedCategory);
    }
}
