package com.sparta.delivery.domainTest.restaurant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import com.sparta.delivery.domain.region.entity.Region;
import com.sparta.delivery.domain.region.repository.RegionRepository;
import com.sparta.delivery.domain.restaurant.dto.RestaurantCreateReqDto;
import com.sparta.delivery.domain.restaurant.dto.RestaurantCreateResDto;
import com.sparta.delivery.domain.restaurant.dto.RestaurantSearchReqDto;
import com.sparta.delivery.domain.restaurant.dto.RestaurantSummaryResDto;
import com.sparta.delivery.domain.restaurant.dto.RestaurantUpdateReqDto;
import com.sparta.delivery.domain.restaurant.entity.Restaurant;
import com.sparta.delivery.domain.restaurant.entity.RestaurantCategory;
import com.sparta.delivery.domain.restaurant.repository.RestaurantCategoryRepository;
import com.sparta.delivery.domain.restaurant.repository.RestaurantRepository;
import com.sparta.delivery.domain.restaurant.service.RestaurantService;
import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.domain.user.repository.UserRepository;
import com.sparta.delivery.global.common.Enums;
import com.sparta.delivery.global.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {

	@InjectMocks
	private RestaurantService restaurantService;

	@Mock
	private RestaurantRepository restaurantRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private RegionRepository regionRepository;

	@Mock
	private RestaurantCategoryRepository categoryRepository;

	@Test
	@DisplayName("가게 목록 조회 성공 - 검색, 페이지 조건 전달 후 summary 반환")
	void getAllRestaurants_Success() {
		// given
		UUID categoryId = UUID.randomUUID();
		UUID regionId = UUID.randomUUID();
		UUID restaurantId = UUID.randomUUID();
		RestaurantSearchReqDto condition = createSearchReqDto(categoryId, regionId, "치킨", true);
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "averageRating"));
		Restaurant restaurant = createRestaurant(restaurantId, categoryId, regionId, "맛있는 치킨집", true, 4.5, 12L);

		given(restaurantRepository.searchRestaurants(eq(categoryId), eq(regionId), eq(true), eq("치킨"), any(Pageable.class)))
				.willReturn(new PageImpl<>(List.of(restaurant), pageable, 1));

		// when
		Page<RestaurantSummaryResDto> result = restaurantService.getAllRestaurants(1L, pageable, condition);

		// then
		assertThat(result.getContent()).hasSize(1);
		RestaurantSummaryResDto response = result.getContent().get(0);
		assertThat(response.getRestaurantId()).isEqualTo(restaurantId);
		assertThat(response.getName()).isEqualTo("맛있는 치킨집");
		assertThat(response.getAverageRating()).isEqualByComparingTo(BigDecimal.valueOf(4.5));
		assertThat(response.getReviewCount()).isEqualTo(12L);
	}

	@Test
	@DisplayName("가게 목록 조회 - name, size 보정")
	void getAllRestaurants_NormalizeNameAndSize() {
		// given
		RestaurantSearchReqDto condition = createSearchReqDto(null, null, null, null);
		Pageable pageable = PageRequest.of(0, 15, Sort.by(Sort.Direction.DESC, "createdAt"));

		given(restaurantRepository.searchRestaurants(eq(null), eq(null), eq(null), eq(""), any(Pageable.class)))
				.willReturn(Page.empty());

		// when
		restaurantService.getAllRestaurants(1L, pageable, condition);

		// then
		ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
		verify(restaurantRepository).searchRestaurants(eq(null), eq(null), eq(null), eq(""), captor.capture());
		Pageable capturedPageable = captor.getValue();
		assertThat(capturedPageable.getPageNumber()).isZero();
		assertThat(capturedPageable.getPageSize()).isEqualTo(10);
		assertThat(capturedPageable.getSort().getOrderFor("createdAt").getDirection()).isEqualTo(Sort.Direction.DESC);
	}

	@Test
	@DisplayName("가게 등록 성공")
	void createRestaurant_Success() {
		// given
		UUID categoryId = UUID.randomUUID();
		UUID regionId = UUID.randomUUID();
		UUID restaurantId = UUID.randomUUID();

		RestaurantCreateReqDto request = createRestaurantCreateReqDto(categoryId, regionId);
		User owner = createUser(Enums.UserRole.OWNER);
		Region region = createRegion(regionId, true);
		RestaurantCategory category = createCategory(categoryId, true);

		given(userRepository.findById(1L)).willReturn(Optional.of(owner));
		given(regionRepository.findById(regionId)).willReturn(Optional.of(region));
		given(categoryRepository.findById(categoryId)).willReturn(Optional.of(category));
		given(restaurantRepository.save(any(Restaurant.class))).willAnswer(invocation -> {
			Restaurant savedRestaurant = invocation.getArgument(0);
			ReflectionTestUtils.setField(savedRestaurant, "id", restaurantId);
			return savedRestaurant;
		});

		// when
		RestaurantCreateResDto response = restaurantService.createRestaurant(1L, request);

		// then
		assertThat(response.getRestaurantId()).isEqualTo(restaurantId);
		assertThat(response.getName()).isEqualTo("test restaurant");
		assertThat(response.getIsOpen()).isTrue();
		verify(restaurantRepository).save(any(Restaurant.class));
	}

	@Test
	@DisplayName("가게 등록 실패 - 사용자를 찾을 수 없음")
	void createRestaurant_Fail_UserNotFound() {
		// given
		RestaurantCreateReqDto request = createRestaurantCreateReqDto(UUID.randomUUID(), UUID.randomUUID());
		given(userRepository.findById(1L)).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> restaurantService.createRestaurant(1L, request))
				.isInstanceOf(ResourceNotFoundException.class);

		verify(restaurantRepository, never()).save(any(Restaurant.class));
	}

	@Test
	@DisplayName("가게 등록 실패 - 사용자가 가게 주인이 아님")
	void createRestaurant_Fail_UserIsNotOwner() {
		// given
		RestaurantCreateReqDto request = createRestaurantCreateReqDto(UUID.randomUUID(), UUID.randomUUID());
		User customer = createUser(Enums.UserRole.CUSTOMER);
		given(userRepository.findById(1L)).willReturn(Optional.of(customer));

		// when & then
		assertThatThrownBy(() -> restaurantService.createRestaurant(1L, request))
				.isInstanceOf(IllegalArgumentException.class);

		verify(restaurantRepository, never()).save(any(Restaurant.class));
	}

	@Test
	@DisplayName("가게 등록 실패 - 지역을 찾을 수 없음")
	void createRestaurant_Fail_RegionNotFound() {
		// given
		UUID categoryId = UUID.randomUUID();
		UUID regionId = UUID.randomUUID();
		RestaurantCreateReqDto request = createRestaurantCreateReqDto(categoryId, regionId);
		User owner = createUser(Enums.UserRole.OWNER);
		given(userRepository.findById(1L)).willReturn(Optional.of(owner));
		given(regionRepository.findById(regionId)).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> restaurantService.createRestaurant(1L, request))
				.isInstanceOf(ResourceNotFoundException.class);

		verify(restaurantRepository, never()).save(any(Restaurant.class));
	}

	@Test
	@DisplayName("가게 등록 실패 - 서비스 가능 지역이 아님")
	void createRestaurant_Fail_RegionIsNotServiceAvailable() {
		// given
		UUID categoryId = UUID.randomUUID();
		UUID regionId = UUID.randomUUID();
		RestaurantCreateReqDto request = createRestaurantCreateReqDto(categoryId, regionId);
		User owner = createUser(Enums.UserRole.OWNER);
		Region region = createRegion(regionId, false);
		given(userRepository.findById(1L)).willReturn(Optional.of(owner));
		given(regionRepository.findById(regionId)).willReturn(Optional.of(region));

		// when & then
		assertThatThrownBy(() -> restaurantService.createRestaurant(1L, request))
				.isInstanceOf(IllegalArgumentException.class);

		verify(restaurantRepository, never()).save(any(Restaurant.class));
	}

	@Test
	@DisplayName("가게 등록 실패 - 카테고리를 찾을 수 없음")
	void createRestaurant_Fail_CategoryNotFound() {
		// given
		UUID categoryId = UUID.randomUUID();
		UUID regionId = UUID.randomUUID();
		RestaurantCreateReqDto request = createRestaurantCreateReqDto(categoryId, regionId);
		User owner = createUser(Enums.UserRole.OWNER);
		Region region = createRegion(regionId, true);
		given(userRepository.findById(1L)).willReturn(Optional.of(owner));
		given(regionRepository.findById(regionId)).willReturn(Optional.of(region));
		given(categoryRepository.findById(categoryId)).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> restaurantService.createRestaurant(1L, request))
				.isInstanceOf(ResourceNotFoundException.class);

		verify(restaurantRepository, never()).save(any(Restaurant.class));
	}

	@Test
	@DisplayName("가게 등록 실패 - 비활성화된 카테고리")
	void createRestaurant_Fail_CategoryIsInactive() {
		// given
		UUID categoryId = UUID.randomUUID();
		UUID regionId = UUID.randomUUID();
		RestaurantCreateReqDto request = createRestaurantCreateReqDto(categoryId, regionId);
		User owner = createUser(Enums.UserRole.OWNER);
		Region region = createRegion(regionId, true);
		RestaurantCategory category = createCategory(categoryId, false);
		given(userRepository.findById(1L)).willReturn(Optional.of(owner));
		given(regionRepository.findById(regionId)).willReturn(Optional.of(region));
		given(categoryRepository.findById(categoryId)).willReturn(Optional.of(category));

		// when & then
		assertThatThrownBy(() -> restaurantService.createRestaurant(1L, request))
				.isInstanceOf(IllegalArgumentException.class);

		verify(restaurantRepository, never()).save(any(Restaurant.class));
	}



	@Test
	@DisplayName("가게 상세 조회 성공")
	void getRestaurantInfo_Success() {
		// given
		UUID categoryId = UUID.randomUUID();
		UUID regionId = UUID.randomUUID();
		UUID restaurantId = UUID.randomUUID();
		Restaurant restaurant = createRestaurant(restaurantId, categoryId, regionId, "test restaurant", true, 4.5, 12L);
		given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));

		// when
		RestaurantSummaryResDto response = restaurantService.getRestaurantInfo(restaurantId);

		// then
		assertThat(response.getRestaurantId()).isEqualTo(restaurantId);
		assertThat(response.getName()).isEqualTo("test restaurant");
		assertThat(response.getAverageRating()).isEqualByComparingTo(BigDecimal.valueOf(4.5));
		assertThat(response.getReviewCount()).isEqualTo(12L);
	}

	@Test
	@DisplayName("가게 상세 조회 실패 - 존재하지 않는 가게")
	void getRestaurantInfo_Fail_RestaurantNotFound() {
		// given
		UUID restaurantId = UUID.randomUUID();
		given(restaurantRepository.findById(restaurantId)).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> restaurantService.getRestaurantInfo(restaurantId))
				.isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	@DisplayName("가게 정보 수정 성공 - 전달된 필드만 변경")
	void updateRestaurant_Success_PartialUpdate() {
		// given
		Long userId = 200L;
		UUID categoryId = UUID.randomUUID();
		UUID regionId = UUID.randomUUID();
		UUID restaurantId = UUID.randomUUID();
		User owner = createUser(Enums.UserRole.OWNER);
		ReflectionTestUtils.setField(owner, "id", userId);
		Restaurant restaurant = createRestaurant(restaurantId, categoryId, regionId, "기존 가게", true, 4.5, 12L);
		ReflectionTestUtils.setField(restaurant, "owner", owner);
		RestaurantUpdateReqDto request = createRestaurantUpdateReqDto("수정 가게", "수정 설명", null, null, false, 20000, null);

		given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
		given(userRepository.findById(userId)).willReturn(Optional.of(owner));

		// when
		RestaurantSummaryResDto response = restaurantService.updateRestaurant(userId, restaurantId, request);

		// then
		assertThat(response.getRestaurantId()).isEqualTo(restaurantId);
		assertThat(response.getName()).isEqualTo("수정 가게");
		assertThat(response.getDescription()).isEqualTo("수정 설명");
		assertThat(response.getIsOpen()).isFalse();
		assertThat(response.getMinOrderAmount()).isEqualTo(20000);
		assertThat(response.getDeliveryFee()).isEqualTo(3000);
	}

	@Test
	@DisplayName("가게 정보 수정 실패 - 존재하지 않는 가게")
	void updateRestaurant_Fail_RestaurantNotFound() {
		// given
		Long userId = 1L;
		UUID restaurantId = UUID.randomUUID();
		RestaurantUpdateReqDto request = createRestaurantUpdateReqDto("수정 가게", null, null, null, null, null, null);
		given(restaurantRepository.findById(restaurantId)).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> restaurantService.updateRestaurant(userId, restaurantId, request))
				.isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	@DisplayName("가게 정보 수정 실패 - 존재하지 않는 사용자")
	void updateRestaurant_Fail_UserNotFound() {
		// given
		Long userId = 1L;
		UUID categoryId = UUID.randomUUID();
		UUID regionId = UUID.randomUUID();
		UUID restaurantId = UUID.randomUUID();
		Restaurant restaurant = createRestaurant(restaurantId, categoryId, regionId, "기존 가게", true, 4.5, 12L);
		RestaurantUpdateReqDto request = createRestaurantUpdateReqDto("수정 가게", null, null, null, null, null, null);

		given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
		given(userRepository.findById(userId)).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> restaurantService.updateRestaurant(userId, restaurantId, request))
				.isInstanceOf(ResourceNotFoundException.class);
	}

	@ParameterizedTest
	@MethodSource("invalidUpdateUsers")
	@DisplayName("가게 정보 수정 실패 - 수정 권한이 없는 사용자는 수정 불가")
	void updateRestaurant_Fail_UserWithoutPermissionCannotUpdate(
			Enums.UserRole requestUserRole,
			Long requestUserId,
			Long restaurantOwnerId
	) {
		// given
		UUID categoryId = UUID.randomUUID();
		UUID regionId = UUID.randomUUID();
		UUID restaurantId = UUID.randomUUID();
		User requestUser = createUser(requestUserRole);
		User restaurantOwner = createUser(Enums.UserRole.OWNER);
		ReflectionTestUtils.setField(requestUser, "id", requestUserId);
		ReflectionTestUtils.setField(restaurantOwner, "id", restaurantOwnerId);
		Restaurant restaurant = createRestaurant(restaurantId, categoryId, regionId, "기존 가게", true, 4.5, 12L);
		ReflectionTestUtils.setField(restaurant, "owner", restaurantOwner);
		RestaurantUpdateReqDto request = createRestaurantUpdateReqDto("수정 가게", null, null, null, null, null, null);

		given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
		given(userRepository.findById(requestUserId)).willReturn(Optional.of(requestUser));

		// when & then
		assertThatThrownBy(() -> restaurantService.updateRestaurant(requestUserId, restaurantId, request))
				.isInstanceOf(IllegalArgumentException.class);
	}

	private static Stream<Arguments> invalidUpdateUsers() {
		return Stream.of(
				Arguments.of(Enums.UserRole.CUSTOMER, 1L, 1L),
				Arguments.of(Enums.UserRole.OWNER, 200L, 201L)
		);
	}

	private RestaurantSearchReqDto createSearchReqDto(UUID categoryId, UUID regionId, String name, Boolean isOpen) {
		RestaurantSearchReqDto request = new RestaurantSearchReqDto();
		ReflectionTestUtils.setField(request, "categoryId", categoryId);
		ReflectionTestUtils.setField(request, "regionId", regionId);
		ReflectionTestUtils.setField(request, "name", name);
		ReflectionTestUtils.setField(request, "isOpen", isOpen);
		return request;
	}

	private Restaurant createRestaurant(UUID restaurantId, UUID categoryId, UUID regionId, String name, boolean isOpen, double averageRating, Long reviewCount) {
		User owner = User.builder()
				.username("owner")
				.password("password")
				.name("owner")
				.phone("010-0000-0000")
				.role(Enums.UserRole.OWNER)
				.build();
		RestaurantCategory category = RestaurantCategory.builder()
				.name("치킨")
				.description("치킨 카테고리")
				.sortOrder(1)
				.isActive(true)
				.build();
		Region region = Region.builder()
				.name("서울 강남구")
				.regionType(Enums.RegionType.SIGUNGU)
				.isServiceAvailable(true)
				.build();
		ReflectionTestUtils.setField(category, "id", categoryId);
		ReflectionTestUtils.setField(region, "id", regionId);

		Restaurant restaurant = Restaurant.create(
				owner,
				category,
				region,
				name,
				name + " 설명",
				"02-1234-5678",
				"서울시 강남구 테헤란로 1",
				"1층",
				"123-45-67890",
				15000,
				3000
		);
		ReflectionTestUtils.setField(restaurant, "id", restaurantId);
		ReflectionTestUtils.setField(restaurant, "isOpen", isOpen);
		restaurant.updateRatingAndCount(averageRating, reviewCount);
		return restaurant;
	}

	private RestaurantCreateReqDto createRestaurantCreateReqDto(UUID categoryId, UUID regionId) {
		RestaurantCreateReqDto request = new RestaurantCreateReqDto();
		ReflectionTestUtils.setField(request, "categoryId", categoryId);
		ReflectionTestUtils.setField(request, "regionId", regionId);
		ReflectionTestUtils.setField(request, "name", "test restaurant");
		ReflectionTestUtils.setField(request, "description", "test description");
		ReflectionTestUtils.setField(request, "phone", "02-1234-5678");
		ReflectionTestUtils.setField(request, "address", "test address");
		ReflectionTestUtils.setField(request, "detailAddress", "1F");
		ReflectionTestUtils.setField(request, "businessNumber", "123-45-67890");
		ReflectionTestUtils.setField(request, "minOrderAmount", 15000);
		ReflectionTestUtils.setField(request, "deliveryFee", 3000);
		return request;
	}

	private RestaurantUpdateReqDto createRestaurantUpdateReqDto(
			String name,
			String description,
			String phone,
			String address,
			Boolean isOpen,
			Integer minOrderAmount,
			Integer deliveryFee
	) {
		RestaurantUpdateReqDto request = new RestaurantUpdateReqDto();
		ReflectionTestUtils.setField(request, "name", name);
		ReflectionTestUtils.setField(request, "description", description);
		ReflectionTestUtils.setField(request, "phone", phone);
		ReflectionTestUtils.setField(request, "address", address);
		ReflectionTestUtils.setField(request, "isOpen", isOpen);
		ReflectionTestUtils.setField(request, "minOrderAmount", minOrderAmount);
		ReflectionTestUtils.setField(request, "deliveryFee", deliveryFee);
		return request;
	}

	private User createUser(Enums.UserRole role) {
		return User.builder()
				.username("user")
				.password("password")
				.name("user")
				.phone("010-0000-0000")
				.role(role)
				.build();
	}

	private Region createRegion(UUID regionId, boolean isServiceAvailable) {
		Region region = Region.builder()
				.name("test region")
				.regionType(Enums.RegionType.SIGUNGU)
				.isServiceAvailable(isServiceAvailable)
				.build();
		ReflectionTestUtils.setField(region, "id", regionId);
		return region;
	}

	private RestaurantCategory createCategory(UUID categoryId, boolean isActive) {
		RestaurantCategory category = RestaurantCategory.builder()
				.name("test category")
				.description("test category description")
				.sortOrder(1)
				.isActive(isActive)
				.build();
		ReflectionTestUtils.setField(category, "id", categoryId);
		return category;
	}
}
