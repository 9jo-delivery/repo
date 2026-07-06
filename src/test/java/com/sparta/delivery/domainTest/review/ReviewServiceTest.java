package com.sparta.delivery.domainTest.review;

import static org.awaitility.Awaitility.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.sparta.delivery.domain.order.Order;
import com.sparta.delivery.domain.restaurant.Restaurant;
import com.sparta.delivery.domain.review.dto.ReviewRequestDto;
import com.sparta.delivery.domain.review.dto.ReviewResponseDto;
import com.sparta.delivery.domain.review.repository.ReviewRepository;
import com.sparta.delivery.domain.review.repository.TempOrderRepository;
import com.sparta.delivery.domain.review.repository.TempRestaurantRepository;
import com.sparta.delivery.domain.review.service.ReviewService;
import com.sparta.delivery.domain.user.enitiy.User;
import com.sparta.delivery.global.common.Enums;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {
	@InjectMocks
	private ReviewService reviewService;

	@Mock
	private ReviewRepository reviewRepository;

	@Mock
	private TempOrderRepository orderRepository;

	@Mock
	private TempRestaurantRepository restaurantRepository;

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
			.hasMessage("자신의 주문에서만 리뷰 가능");
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
		given(orderRepository.existsById(orderId)).willReturn(true); //  당연히 order에는 Id가 있을테니깐 true 반환됨
		// 그러면 given -> when -> then 여기서 내가 예상하는 결과값은 넌 이미 리뷰 작성했으니 못써 임마 이건데?

		org.assertj.core.api.Assertions.assertThatThrownBy(() ->
			reviewService.createReview(orderId, requestDto, customerId)
			).isInstanceOf(IllegalArgumentException.class)
			.hasMessage("이미 리뷰하셧음다"); // 근데 assertionError가 나옴 내가 예상한 값이 아니라는데?
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
		Order order = Order.builder()
			.customer(user)
			.restaurant(restaurant)
			.orderStatus(Enums.OrderStatus.COMPLETED)
			.build();

		ReflectionTestUtils.setField(order, "id", orderId);
		return order;
	}

}
