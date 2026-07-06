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

		given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
		given(reviewRepository.existsByOrderId(orderId)).willReturn(false);
		ReviewResponseDto response = reviewService.createReview(orderId, requestDto, customerId);

		assertEquals(5, response.getRating());

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
