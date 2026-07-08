package com.sparta.delivery.domain.review.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.delivery.domain.order.entity.Order;

import com.sparta.delivery.domain.order.entity.Order;
import com.sparta.delivery.domain.restaurant.entity.Restaurant;
import com.sparta.delivery.domain.review.dto.ReviewRequestDto;
import com.sparta.delivery.domain.review.dto.ReviewResponseDto;
import com.sparta.delivery.domain.review.dto.ReviewSearchCondition;
import com.sparta.delivery.domain.review.entity.Review;
import com.sparta.delivery.domain.review.repository.ReviewRepository;
import com.sparta.delivery.domain.review.repository.TempOrderRepository;
import com.sparta.delivery.domain.restaurant.repository.TempRestaurantRepository;
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

	@Transactional
	public ReviewResponseDto createReview(UUID orderId, ReviewRequestDto request, Long customerId) {

		// 주문 검증
		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

		if (order.getOrderStatus() != Enums.OrderStatus.COMPLETED) {
			throw new IllegalArgumentException("배달이 완료된 주문만 리뷰 작성 가능");
		}

		// 권한 검증 (이 주문을 한 고객과 현재 리뷰를 쓰려는 고객이 일치하는가?)

		if (!order.getCustomer().getId().equals(customerId)) {
			throw new IllegalArgumentException("자신의 주문에서만 리뷰 작성 가능");
		}

		// 리류 중복 작성 검증
		if (reviewRepository.existsByOrderId(orderId)){
			throw new IllegalArgumentException("해당 주문에 이미 작성한 리뷰 존재");
		}

		// 리뷰 엔티티 생성
		Review review = Review.create(order, request.getRating(), request.getContent());

		reviewRepository.save(review);

		// dirty Check
		updateRestaurantRatingAndCount(order.getRestaurant());

		return ReviewResponseDto.from(review);

	}

	public ReviewResponseDto getReview(UUID reviewId) {
		Review review = reviewRepository.findById(reviewId).orElseThrow(()
		-> new IllegalArgumentException("해당리뷰 없음"));
		return ReviewResponseDto.from(review);
	}

	public Page<ReviewResponseDto> getRestaurantReviews(UUID restaurantId, ReviewSearchCondition condition, Pageable pageable) {
		if (!restaurantRepository.existsById(restaurantId)) {
			throw new IllegalArgumentException("해당 레스토랑없음");
		}
		// QureryDsl 메서드
		Page<Review> reviewPage = reviewRepository.searchRestaurantReviews(restaurantId, condition, pageable);
		return reviewPage.map(ReviewResponseDto::from);
	}

	@Transactional
	public ReviewResponseDto updateReview(UUID reviewId, ReviewRequestDto request, Long customerId) {
		// 검증 (리뷰 유뮤, 작성자 확인)
		Review review = reviewRepository.findById(reviewId).orElseThrow(()
		-> new IllegalArgumentException("작성한 리뷰내역 없음"));


		if(!review.getCustomer().getId().equals(customerId)) {
			throw new IllegalArgumentException("자신의 리뷰만 업데이트 가능");
		}

		// 값 수정(Dirty Check)
		// 엔티티 내부의 값을 변경하면, 이 메서드가 끝날 때 JPA가 알아서 UPDATE 쿼리 날림
		// updatedAt 역시 BaseEntity가 알아서 현재 시간으로 업데이트함..
		review.updateContentAndRating(request.getContent(),request.getRating());

		updateRestaurantRatingAndCount(review.getRestaurant());

		return ReviewResponseDto.from(review);

	}

	@Transactional
	public UUID deleteReview(UUID reviewId, Long customerId) {
		Review review = reviewRepository.findById(reviewId).orElseThrow(()
		-> new IllegalArgumentException("삭제 가능한 리뷰 없음"));

		if (!review.getCustomer().getId().equals(customerId)) {
			throw new IllegalArgumentException("자신의 리뷰만 삭제 가능");
		}
		Enums.UserRole isOwner = review.getCustomer().getRole();

		if (isOwner == Enums.UserRole.OWNER || isOwner == Enums.UserRole.MASTER) {
			throw new IllegalArgumentException("삭제 권한 없음");
		}

		reviewRepository.delete(review);
		updateRestaurantRatingAndCount(review.getRestaurant());
		return reviewId;
	}

	private void updateRestaurantRatingAndCount(Restaurant restaurant) {
		// softDelete 상태?가 아닌 리뷰들의 총갯수 가져옴
		Long reviewCount = reviewRepository.countByRestaurantIdAndIsDeletedFalse(restaurant.getId());

		// 찾아온 식당의 리뷰둘의 평균 평점 계산식
		// JQPL
		double averageRating = reviewRepository.calculateAverageRatingByRestaurantId(restaurant.getId());
		restaurant.updateRatingAndCount(averageRating, reviewCount);
	}

}
