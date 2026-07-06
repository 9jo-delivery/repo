package com.sparta.delivery.domainTest.review;

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
import com.sparta.delivery.domain.review.repository.ReviewRepository;
import com.sparta.delivery.domain.review.repository.TempOrderRepository;
import com.sparta.delivery.domain.review.repository.TempRestaurantRepository;
import com.sparta.delivery.domain.review.service.ReviewService;
import com.sparta.delivery.domain.user.enitiy.User;

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

		createFakeUser(customerId);





	}



	// 테스트 코드 의존성 분리 User, Restaurant, Order
	private User createFakeUser(Long userId) {
		User user = User.builder()
			.username("test")
			.build();
		ReflectionTestUtils.setField(user, "userId", userId);
		return user;
	}

	private Restaurant createFakeRestaurant(UUID restaurantId) {
		Restaurant restaurant = Restaurant.builder()
			.name("testRestaurant")
			.build();
		ReflectionTestUtils.setField(restaurant, "restaurantId", restaurantId);
		return restaurant;
	}

	private Order createFakeOrder(UUID orderId, Restaurant restaurant, User user) {
		Order order = Order.builder()
			.customer(user)
			.restaurant(restaurant)
			.build();

		ReflectionTestUtils.setField(order, "orderId", orderId);
		return order;
	}



}
