package com.sparta.delivery.domainTest.restaurant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import com.sparta.delivery.domain.restaurant.dto.CategoryCreateReqDto;
import com.sparta.delivery.domain.restaurant.dto.CategoryCreateResDto;
import com.sparta.delivery.domain.restaurant.dto.CategorySearchReqDto;
import com.sparta.delivery.domain.restaurant.dto.CategorySummaryResDto;
import com.sparta.delivery.domain.restaurant.dto.CategoryUpdateReqDto;
import com.sparta.delivery.domain.restaurant.entity.RestaurantCategory;
import com.sparta.delivery.domain.restaurant.repository.RestaurantCategoryRepository;
import com.sparta.delivery.domain.restaurant.service.RestaurantCategoryService;
import com.sparta.delivery.global.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class RestaurantCategoryServiceTest {

	@InjectMocks
	private RestaurantCategoryService restaurantCategoryService;

	@Mock
	private RestaurantCategoryRepository restaurantCategoryRepository;

	@Test
	@DisplayName("카테고리 생성 성공 - sortOrder, isActive 기본값 적용")
	void createCategory_Success() {
		// given
		CategoryCreateReqDto request = createCategoryCreateReqDto("치킨", "치킨 카테고리", null);
		UUID categoryId = UUID.randomUUID();

		given(restaurantCategoryRepository.findByName("치킨")).willReturn(Optional.empty());
		given(restaurantCategoryRepository.save(any(RestaurantCategory.class)))
				.willAnswer(invocation -> {
					RestaurantCategory category = invocation.getArgument(0);
					ReflectionTestUtils.setField(category, "id", categoryId);
					return category;
				});

		// when
		CategoryCreateResDto response = restaurantCategoryService.createCategory(request);

		// then
		assertThat(response.getId()).isEqualTo(categoryId);
		assertThat(response.getName()).isEqualTo("치킨");

		ArgumentCaptor<RestaurantCategory> captor = ArgumentCaptor.forClass(RestaurantCategory.class);
		verify(restaurantCategoryRepository).save(captor.capture());
		RestaurantCategory savedCategory = captor.getValue();
		assertThat(savedCategory.getSortOrder()).isZero();
		assertThat(savedCategory.getIsActive()).isTrue();
	}

	@Test
	@DisplayName("카테고리 생성 실패 - 중복 이름 예외 처리")
	void createCategory_Fail_DuplicateName() {
		// given
		CategoryCreateReqDto request = createCategoryCreateReqDto("치킨", "치킨 카테고리", 1);
		RestaurantCategory existingCategory = createCategory(UUID.randomUUID(), "치킨", "기존 카테고리", 1, true);

		given(restaurantCategoryRepository.findByName("치킨")).willReturn(Optional.of(existingCategory));

		// when & then
		assertThatThrownBy(() -> restaurantCategoryService.createCategory(request))
				.isInstanceOf(IllegalArgumentException.class);

		verify(restaurantCategoryRepository, never()).save(any(RestaurantCategory.class));
	}

	@Test
	@DisplayName("카테고리 목록 검색 성공 - 검색, 페이지 조건 전달하고 페이지 변환")
	void getAllCategories_Success() {
		// given
		CategorySearchReqDto condition = new CategorySearchReqDto();
		condition.setPage(0);
		condition.setSize(30);
		condition.setName("치킨");
		condition.setIsActive(true);

		RestaurantCategory category = createCategory(UUID.randomUUID(), "치킨", "치킨 카테고리", 1, true);
		given(restaurantCategoryRepository.searchCategories(eq("치킨"), eq(true), any(Pageable.class)))
				.willReturn(new PageImpl<>(List.of(category)));

		// when
		Page<CategorySummaryResDto> result = restaurantCategoryService.getAllCategories(condition);

		// then
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).getName()).isEqualTo("치킨");
		assertThat(result.getContent().get(0).getDescription()).isEqualTo("치킨 카테고리");
	}

	@Test
	@DisplayName("카테고리 목록 검색 - page, size, null name 보정")
	void getAllCategories_NormalizeSearchCondition() {
		// given
		CategorySearchReqDto condition = new CategorySearchReqDto();
		condition.setPage(-1);
		condition.setSize(15);
		condition.setName(null);
		condition.setIsActive(null);

		given(restaurantCategoryRepository.searchCategories(eq(""), eq(null), any(Pageable.class)))
				.willReturn(Page.empty());

		// when
		restaurantCategoryService.getAllCategories(condition);

		// then
		ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
		verify(restaurantCategoryRepository).searchCategories(eq(""), eq(null), captor.capture());
		Pageable pageable = captor.getValue();
		assertThat(pageable.getPageNumber()).isZero();
		assertThat(pageable.getPageSize()).isEqualTo(10);
	}

	@Test
	@DisplayName("카테고리 단건 조회 성공")
	void getCategoryInfo_Success() {
		// given
		UUID categoryId = UUID.randomUUID();
		RestaurantCategory category = createCategory(categoryId, "치킨", "치킨 카테고리", 1, true);

		given(restaurantCategoryRepository.findById(categoryId)).willReturn(Optional.of(category));

		// when
		CategorySummaryResDto response = restaurantCategoryService.getCategoryInfo(categoryId);

		// then
		assertThat(response.getId()).isEqualTo(categoryId);
		assertThat(response.getName()).isEqualTo("치킨");
		assertThat(response.getIsActive()).isTrue();
	}

	@Test
	@DisplayName("카테고리 단건 조회 실패 - 존재하지 않는 id")
	void getCategoryInfo_Fail_NotFound() {
		// given
		UUID categoryId = UUID.randomUUID();
		given(restaurantCategoryRepository.findById(categoryId)).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> restaurantCategoryService.getCategoryInfo(categoryId))
				.isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	@DisplayName("카테고리 수정 성공 - 전달된 필드만 변경")
	void updateCategory_Success_PartialUpdate() {
		// given
		UUID categoryId = UUID.randomUUID();
		RestaurantCategory category = createCategory(categoryId, "치킨", "기존 설명", 1, true);
		CategoryUpdateReqDto request = createCategoryUpdateReqDto(null, "수정된 설명", null, false);

		given(restaurantCategoryRepository.findById(categoryId)).willReturn(Optional.of(category));

		// when
		CategorySummaryResDto response = restaurantCategoryService.updateCategory(categoryId, request);

		// then
		assertThat(response.getName()).isEqualTo("치킨");
		assertThat(response.getDescription()).isEqualTo("수정된 설명");
		assertThat(response.getSortOrder()).isEqualTo(1);
		assertThat(response.getIsActive()).isFalse();
	}

	@Test
	@DisplayName("카테고리 수정 성공 - 기존 이름 동일 전달은 허용")
	void updateCategory_Success_SameName() {
		// given
		UUID categoryId = UUID.randomUUID();
		RestaurantCategory category = createCategory(categoryId, "치킨", "기존 설명", 1, true);
		CategoryUpdateReqDto request = createCategoryUpdateReqDto("치킨", "수정된 설명", null, null);

		given(restaurantCategoryRepository.findById(categoryId)).willReturn(Optional.of(category));
		given(restaurantCategoryRepository.findByName("치킨")).willReturn(Optional.of(category));

		// when
		CategorySummaryResDto response = restaurantCategoryService.updateCategory(categoryId, request);

		// then
		assertThat(response.getName()).isEqualTo("치킨");
		assertThat(response.getDescription()).isEqualTo("수정된 설명");
	}

	@Test
	@DisplayName("카테고리 수정 실패 - 중복된 카테고리 이름")
	void updateCategory_Fail_DuplicateName() {
		// given
		UUID categoryId = UUID.randomUUID();
		RestaurantCategory category = createCategory(categoryId, "치킨", "기존 설명", 1, true);
		RestaurantCategory existingCategory = createCategory(UUID.randomUUID(), "피자", "기존 피자", 2, true);
		CategoryUpdateReqDto request = createCategoryUpdateReqDto("피자", null, null, null);

		given(restaurantCategoryRepository.findById(categoryId)).willReturn(Optional.of(category));
		given(restaurantCategoryRepository.findByName("피자")).willReturn(Optional.of(existingCategory));

		// when & then
		assertThatThrownBy(() -> restaurantCategoryService.updateCategory(categoryId, request))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("카테고리 수정 실패 - 존재하지 않는 id")
	void updateCategory_Fail_NotFound() {
		// given
		UUID categoryId = UUID.randomUUID();
		CategoryUpdateReqDto request = createCategoryUpdateReqDto("치킨", null, null, null);
		given(restaurantCategoryRepository.findById(categoryId)).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> restaurantCategoryService.updateCategory(categoryId, request))
				.isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	@DisplayName("카테고리 삭제 성공")
	void deleteCategory_Success() {
		// given
		UUID categoryId = UUID.randomUUID();
		RestaurantCategory category = createCategory(categoryId, "치킨", "치킨 카테고리", 1, true);

		given(restaurantCategoryRepository.findById(categoryId)).willReturn(Optional.of(category));

		// when
		restaurantCategoryService.deleteCategory(categoryId);

		// then
		verify(restaurantCategoryRepository).delete(category);
	}

	@Test
	@DisplayName("카테고리 삭제 실패 - 존재하지 않는 id")
	void deleteCategory_Fail_NotFound() {
		// given
		UUID categoryId = UUID.randomUUID();
		given(restaurantCategoryRepository.findById(categoryId)).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> restaurantCategoryService.deleteCategory(categoryId))
				.isInstanceOf(ResourceNotFoundException.class);
		verify(restaurantCategoryRepository, never()).delete(any(RestaurantCategory.class));
	}

	private CategoryCreateReqDto createCategoryCreateReqDto(String name, String description, Integer sortOrder) {
		CategoryCreateReqDto request = new CategoryCreateReqDto();
		ReflectionTestUtils.setField(request, "name", name);
		ReflectionTestUtils.setField(request, "description", description);
		ReflectionTestUtils.setField(request, "sortOrder", sortOrder);
		return request;
	}

	private CategoryUpdateReqDto createCategoryUpdateReqDto(String name, String description, Integer sortOrder, Boolean isActive) {
		CategoryUpdateReqDto request = new CategoryUpdateReqDto();
		ReflectionTestUtils.setField(request, "name", name);
		ReflectionTestUtils.setField(request, "description", description);
		ReflectionTestUtils.setField(request, "sortOrder", sortOrder);
		ReflectionTestUtils.setField(request, "isActive", isActive);
		return request;
	}

	private RestaurantCategory createCategory(UUID id, String name, String description, Integer sortOrder, Boolean isActive) {
		RestaurantCategory category = RestaurantCategory.builder()
				.name(name)
				.description(description)
				.sortOrder(sortOrder)
				.isActive(isActive)
				.build();
		ReflectionTestUtils.setField(category, "id", id);
		return category;
	}
}
