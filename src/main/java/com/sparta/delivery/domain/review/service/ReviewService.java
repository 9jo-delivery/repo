package com.sparta.delivery.domain.review.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.delivery.domain.order.Order;
import com.sparta.delivery.domain.restaurant.Restaurant;
import com.sparta.delivery.domain.review.dto.ReviewRequestDto;
import com.sparta.delivery.domain.review.dto.ReviewResponseDto;
import com.sparta.delivery.domain.review.entity.Review;
import com.sparta.delivery.domain.review.repository.ReviewRepository;
import com.sparta.delivery.domain.review.repository.TempOrderRepository;
import com.sparta.delivery.domain.review.repository.TempRestaurantRepository;
import com.sparta.delivery.domain.user.enitiy.User;
import com.sparta.delivery.global.common.Enums;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "Review API")
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final TempOrderRepository orderRepository;
	private final TempRestaurantRepository restaurantRepository;

	@Transactional //데이터가 변경(저장) 되므로 트랜잭션!
	public ReviewResponseDto createReview(UUID orderId, ReviewRequestDto request, Long customerId) {

		// 주문 검증
		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

		if (order.getOrderStatus() != Enums.OrderStatus.COMPLETED) {
			throw new IllegalArgumentException("배달이 완료된 주문만 리뷰 작성 가능");
		}

		// 권한 검증 (이 주문을 한 고객과 현재 리뷰를 쓰려는 고객이 일치하는가?)

		if (order.getCustomer().getId().equals(customerId)) {
			throw new IllegalArgumentException("자신의 주문에서만 리뷰 작성 가능");
		}

		// 리류 중복 작성 검증
		if (reviewRepository.existsByOrderId(orderId)){
			throw new IllegalArgumentException("해당 주문에 이미 작성한 리뷰 존재");
		}

		// 리뷰 엔티티 생성
		Review review = Review.builder()
			.order(order)
			.restaurant(order.getRestaurant())
			.customer(order.getCustomer())
			.rating(request.getRating())
			.content(request.getContent())
			.build();

		reviewRepository.save(review);

		return null;

	}
}
