package com.sparta.delivery.domain.restaurant.service;

import com.sparta.delivery.domain.restaurant.dto.request.CategoryCreateReqDto;
import com.sparta.delivery.domain.restaurant.dto.request.CategorySearchReqDto;
import com.sparta.delivery.domain.restaurant.dto.request.CategoryUpdateReqDto;
import com.sparta.delivery.domain.restaurant.dto.response.CategoryCreateResDto;
import com.sparta.delivery.domain.restaurant.dto.response.CategorySummaryResDto;
import com.sparta.delivery.domain.restaurant.entity.RestaurantCategory;
import com.sparta.delivery.domain.restaurant.repository.RestaurantCategoryRepository;
import com.sparta.delivery.domain.user.repository.UserRepository;
import com.sparta.delivery.global.exception.ResourceNotFoundException;
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
    private final UserRepository userRepository;

    public RestaurantCategoryService(RestaurantCategoryRepository rcRepository, UserRepository userRepository) {
        this.rcRepository = rcRepository;
        this.userRepository = userRepository;
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

    public int validatePageSize(int size) {
        if (size == 10 || size == 30 || size == 50) {
            return size;
        }
        return 10;
    }

    public CategorySummaryResDto getCategoryInfo(UUID id) {
        RestaurantCategory category = rcRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 카테고리입니다."));

        return new CategorySummaryResDto(category);
    }

    @Transactional
    public CategorySummaryResDto updateCategory(UUID id, CategoryUpdateReqDto categoryUpdateReqDto) {
        RestaurantCategory category = rcRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 카테고리입니다."));

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

    // MASTER (현재 정책상 manager도 추가)
    @Transactional
    public void deleteCategory(Long userId, UUID categoryId) {
        RestaurantCategory category = rcRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 카테고리입니다."));
        userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 사용자입니다."));

        category.markAsDeleted(userId);
    }
}
