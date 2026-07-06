package com.sparta.delivery.domain.restaurant.service;

import com.sparta.delivery.domain.restaurant.dto.CategorySummaryResDto;
import com.sparta.delivery.domain.restaurant.dto.CategorySearchReqDto;
import com.sparta.delivery.domain.restaurant.dto.CategoryReqDto;
import com.sparta.delivery.domain.restaurant.dto.CategoryCreateResDto;
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
    public CategoryCreateResDto createCategory(CategoryReqDto categoryReqDto) { //TODO: 권한 관련 부분 추후 추가 예정
        // 중복 카테고리 여부 검증
        Optional<RestaurantCategory> category = rcRepository.findByName(categoryReqDto.getName());
        if (category.isPresent()) {
            throw new IllegalArgumentException("이미 등록되어 있는 카테고리입니다.");
        }

        // 값 체크
        Integer sortOrder = categoryReqDto.getSortOrder() == null ? 0 : categoryReqDto.getSortOrder();

        // 객체 생성
        RestaurantCategory restaurantCategory = RestaurantCategory.builder()
                .name(categoryReqDto.getName())
                .description(categoryReqDto.getDescription())
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
}
