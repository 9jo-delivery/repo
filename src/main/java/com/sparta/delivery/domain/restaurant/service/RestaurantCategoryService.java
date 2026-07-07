package com.sparta.delivery.domain.restaurant.service;

import com.sparta.delivery.domain.restaurant.dto.*;
import com.sparta.delivery.domain.restaurant.entity.RestaurantCategory;
import com.sparta.delivery.domain.restaurant.repository.RestaurantCategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class RestaurantCategoryService {
    private final RestaurantCategoryRepository rcRepository;

    public RestaurantCategoryService(RestaurantCategoryRepository rcRepository) {
        this.rcRepository = rcRepository;
    }

    @Transactional
    public CategoryCreateResDto createCategory(CategoryCreateReqDto categoryCreateReqDto) {
        // 중복 카테고리 여부 검증
        Optional<RestaurantCategory> category = rcRepository.findByName(categoryCreateReqDto.getName());
        if (category.isPresent()) {
            throw new IllegalArgumentException("이미 등록되어 있는 카테고리입니다.");
        }

        // 값 체크
        Integer sortOrder = categoryCreateReqDto.getSortOrder() == null ? 0 : categoryCreateReqDto.getSortOrder();

        // 객체 생성
        RestaurantCategory restaurantCategory = RestaurantCategory.builder()
                .name(categoryCreateReqDto.getName())
                .description(categoryCreateReqDto.getDescription())
                .sortOrder(sortOrder)
                .isActive(true)
                .build();

        RestaurantCategory savedCategory = rcRepository.save(restaurantCategory);
        return new CategoryCreateResDto(savedCategory);
    }

    @Transactional(readOnly = true)
    public Page<CategorySummaryResDto> getAllCategories(CategorySearchReqDto condition) {
        // 음수 입력 시 보정
        int validatedPage = Math.max(condition.getPage(), 0);

        int validatedSize = validatePageSize(condition.getSize());

        Pageable pageable = PageRequest.of(
                validatedPage,
                validatedSize,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        String name = condition.getName() == null ? "" : condition.getName();

        return rcRepository.searchCategories(name, condition.getIsActive(), pageable)
                .map(CategorySummaryResDto::new);
    }

    private int validatePageSize(int size) {
        if (size == 10 || size == 30 || size == 50) {
            return size;
        }
        return 10;
    }

    public CategorySummaryResDto getCategoryInfo(UUID id) {
        RestaurantCategory category = rcRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));

        return new CategorySummaryResDto(category);
    }

    @Transactional
    public CategorySummaryResDto updateCategory(UUID id, CategoryUpdateReqDto categoryUpdateReqDto) {
        RestaurantCategory category = rcRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));

        // 수정하려는 카테고리명이 이미 있는지 존재 여부 검증 (카테고리명은 고유값이어야 함)
        if (categoryUpdateReqDto.getName() != null &&
                rcRepository.findByName(categoryUpdateReqDto.getName()).isPresent() &&
                !category.getName().equals(categoryUpdateReqDto.getName())
        ) {
            throw new IllegalArgumentException("수정하려는 카테고리명이 이미 목록에 존재합니다.");
        }

        category.update(
                categoryUpdateReqDto.getName(),
                categoryUpdateReqDto.getDescription(),
                categoryUpdateReqDto.getSortOrder(),
                categoryUpdateReqDto.getIsActive()
        );

        return new CategorySummaryResDto(category);
    }

    @Transactional
    public void deleteCategory(UUID id) {
        RestaurantCategory category = rcRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));

        rcRepository.delete(category);
    }
}
