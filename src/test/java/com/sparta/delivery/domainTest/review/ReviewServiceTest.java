package com.sparta.delivery.domainTest.review;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import com.sparta.delivery.domain.order.entity.Order;

import com.sparta.delivery.domain.restaurant.entity.Restaurant;
import com.sparta.delivery.domain.review.dto.ReviewRequestDto;
import com.sparta.delivery.domain.review.dto.ReviewResponseDto;
import com.sparta.delivery.domain.review.dto.ReviewSearchCondition;
import com.sparta.delivery.domain.review.entity.Review;
import com.sparta.delivery.domain.review.repository.ReviewRepository;
import com.sparta.delivery.domain.review.repository.TempOrderRepository;
import com.sparta.delivery.domain.restaurant.repository.RestaurantRepository;
import com.sparta.delivery.domain.review.service.ReviewService;

import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.global.common.Enums;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.assertj.core.api.Assertions.assertThat;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {
	@InjectMocks
	private ReviewService reviewService;

	@Mock
	private ReviewRepository reviewRepository;

	@Mock
	private TempOrderRepository orderRepository;

	@Mock
	private RestaurantRepository restaurantRepository;

	@Test
	@DisplayName("리뷰 생성 성공 테스트")
	void createReview_Success() {
		// Given
		Long customerId = 1L;
		UUID restaurantId = UUID.randomUUID();
		UUID orderId = UUID.randomUUID();
		User customer = createFakeUser(customerId);

		Restaurant restaurant = createFakeRestaurant(restaurantId);
		Order order = createFakeOrder(orderId,customer,restaurant);
		ReviewRequestDto  requestDto = new ReviewRequestDto(5, "Good");

		// When
		given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
		given(reviewRepository.existsByOrderId(orderId)).willReturn(false);
		ReviewResponseDto response = reviewService.createReview(orderId, requestDto, customerId);

		// Then
		assertEquals(5, response.getRating());
	}

	@Test
	@DisplayName("리뷰 생성 실패 테스트- 본인이 아닐경우")
	void createReview_Fail() {
		Long orderOwnerId = 1L;
		Long falseOwnerId = 2L;
		UUID restaurantId = UUID.randomUUID();
		UUID orderId = UUID.randomUUID();

		User customer = createFakeUser(orderOwnerId);
		Restaurant restaurant = createFakeRestaurant(restaurantId);
		Order order = createFakeOrder(orderId,customer,restaurant);
		ReviewRequestDto  requestDto = new ReviewRequestDto(5, "Good");


		given(orderRepository.findById(orderId)).willReturn(Optional.of(order));

		org.assertj.core.api.Assertions.assertThatThrownBy(() -> reviewService.createReview(orderId, requestDto, falseOwnerId))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("자신의 주문에서만 리뷰 작성 가능");
	}

	@Test
	@DisplayName("리뷰 생성 실패 - 작성한 리뷰가 존재하는 경우(도배방지)")
	void createReview_Fail_ReviewExist() {
		Long customerId = 1L;
		UUID orderId = UUID.randomUUID();
		UUID restaurantId = UUID.randomUUID();

		User customer = createFakeUser(customerId);
		Restaurant restaurant = createFakeRestaurant(restaurantId);
		Order order = createFakeOrder(orderId,customer,restaurant);
		ReviewRequestDto  requestDto = new ReviewRequestDto(5, "Good");

		given(orderRepository.findById(orderId)).willReturn(Optional.of(order)); // optional로 감싼 객체(Order)가 반환
		given(reviewRepository.existsByOrderId(orderId)).willReturn(true); // 서비스 로직에서 reviewRepo에 exist 조건을 걸었는데 엉뚱한데서 찾음
		// 그럼 당연히 false 뱉음


		org.assertj.core.api.Assertions.assertThatThrownBy(() ->
			reviewService.createReview(orderId, requestDto, customerId)
			).isInstanceOf(IllegalArgumentException.class)
			.hasMessage("해당 주문에 이미 작성한 리뷰 존재"); // 테스트에선 반드시 서비스 코드에서 던지는 예외 메시지 일치시켜야함
	}

	@Test
	@DisplayName("리뷰 수정 - 내용과 별점이 작성이 실제 반영됨-> dirtyCheck")
	void updateReview_Success() {
		Long customerId = 1L;
		UUID orderId = UUID.randomUUID();
		UUID restaurantId = UUID.randomUUID();
		UUID reviewId = UUID.randomUUID();

		User customer = createFakeUser(customerId);
		Restaurant restaurant = createFakeRestaurant(restaurantId);
		Order order = createFakeOrder(orderId,customer,restaurant);

		Review existReview = Review.create(order, 3, "so so");
		ReflectionTestUtils.setField(existReview, "id", reviewId);
		ReviewRequestDto newRequest = new ReviewRequestDto(5, "Good");
		given(reviewRepository.findById(reviewId)).willReturn(Optional.of(existReview));


		// when
		ReviewResponseDto newResponse = reviewService.updateReview(reviewId, newRequest, customerId);

		// then
		assertEquals(5, newResponse.getRating());
		assertEquals("Good", newResponse.getContent());
	}

	@Test
	@DisplayName("식당 리뷰 목록 조회 성공 - 검색 조건 및 페이징 처리")
	void getRestaurantReviews_Success() {
		// Given
		Long customerId = 1L;
		UUID orderId = UUID.randomUUID();
		UUID restaurantId = UUID.randomUUID();
		User customer = createFakeUser(customerId);
		Restaurant restaurant = createFakeRestaurant(restaurantId);
		Order order = createFakeOrder(orderId, customer, restaurant);

		// 💡 1. 검색 조건을 담을 DTO (주문서) 생성
		// (조건이 없는 전체 조회 상황을 가정 생성자 파라미터에 null
		ReviewSearchCondition condition = new ReviewSearchCondition(null);

		Pageable pageable = PageRequest.of(0, 10,
			Sort.by(Sort.Direction.DESC, "createdAt"));

		List<Review> reviews = List.of(
			Review.create(order, 3, "so so"),
			Review.create(order, 4, "not bad")
		);
		Page<Review> page = new PageImpl<>(reviews, pageable, reviews.size());

		given(restaurantRepository.existsById(restaurantId)).willReturn(true);

		// 핵심 변경점: 기존 findAllByRestaurantId 대신 새로 만든 QueryDSL 메서드로 변경
		given(reviewRepository.searchRestaurantReviews(eq(restaurantId), any(ReviewSearchCondition.class), eq(pageable)))
			.willReturn(page);


		// When
		Page<ReviewResponseDto> result = reviewService.getRestaurantReviews(restaurantId, condition, pageable);


		// Then
		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(2);

		assertThat(result.getContent().get(0))
			.returns("so so", ReviewResponseDto::getContent)
			.returns(3, ReviewResponseDto::getRating);
	}




	// 테스트 코드 의존성 분리 User, Restaurant, Order
	private User createFakeUser(Long userId) {
		User user = User.builder()
			.username("test")
			.build();
		ReflectionTestUtils.setField(user, "id", userId);
		return user;
	}

	private Restaurant createFakeRestaurant(UUID restaurantId) {
		Restaurant restaurant = Restaurant.builder()
			.name("testRestaurant")
			.build();
		ReflectionTestUtils.setField(restaurant, "id", restaurantId);
		return restaurant;
	}

	private Order createFakeOrder(UUID orderId, User user, Restaurant restaurant) {
		Order order = Order.create(user, restaurant, "test-01", "강남", "101", "123");
		ReflectionTestUtils.setField(order, "id", orderId);
		ReflectionTestUtils.setField(order, "orderStatus", Enums.OrderStatus.COMPLETED);
		return order;
	}

}
