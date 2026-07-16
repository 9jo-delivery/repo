package com.sparta.delivery.domain.review.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import com.sparta.delivery.domain.order.entity.Order;
import com.sparta.delivery.domain.order.repository.OrderRepository;
import com.sparta.delivery.domain.restaurant.entity.Restaurant;
import com.sparta.delivery.domain.restaurant.repository.RestaurantRepository;
import com.sparta.delivery.domain.review.dto.ReviewRequestDto;
import com.sparta.delivery.domain.review.dto.ReviewResponseDto;
import com.sparta.delivery.domain.review.dto.ReviewSearchCondition;
import com.sparta.delivery.domain.review.entity.Review;
import com.sparta.delivery.domain.review.repository.ReviewRepository;
import com.sparta.delivery.global.common.Enums;
import com.sparta.delivery.global.exception.ResourceNotFoundException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "Review API")
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final OrderRepository orderRepository;
	private final RestaurantRepository restaurantRepository;

	@Transactional
	public ReviewResponseDto createReview(UUID orderId, @Valid @RequestBody ReviewRequestDto request, Long customerId) {

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
		if (reviewRepository.existsByOrderId(orderId)) {
			throw new IllegalArgumentException("해당 주문에 이미 작성한 리뷰 존재");
		}

		// 리뷰 엔티티 생성
		Review review = Review.create(order, request.getRating(), request.getContent());

		reviewRepository.save(review);

		// 수정 및 삭제와 다르게 생성은 insert 쿼리가 바로 날라가지 않고 쓰기지연 저장소에 있음
		// 따라서 DB에 강제로 Insert하기 위해서 flush를 호출해서 락을 걸기전에 그 상황을 DB에 insert쿼리를 전송
		reviewRepository.flush();

		updateRestaurantRatingWithLock(order.getRestaurant().getId());

		return ReviewResponseDto.from(review);

	}

	public ReviewResponseDto getReview(UUID reviewId) {
		Review review = reviewRepository.findById(reviewId).orElseThrow(()
			-> new IllegalArgumentException("해당리뷰 없음"));
		return ReviewResponseDto.from(review);
	}

	public Page<ReviewResponseDto> getRestaurantReviews(UUID restaurantId, ReviewSearchCondition condition,
		Pageable pageable) {
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

		if (!review.getCustomer().getId().equals(customerId)) {
			throw new IllegalArgumentException("자신의 리뷰만 업데이트 가능");
		}

		// 값 수정(Dirty Check)
		// 엔티티 내부의 값을 변경하면, 이 메서드가 끝날 때 JPA가 알아서 UPDATE 쿼리 날림
		// updatedAt 역시 BaseEntity가 알아서 현재 시간으로 업데이트함..
		review.updateContentAndRating(request.getContent(), request.getRating());
		// 비관적 락 적용 평점/개수 업데이트 처리
		updateRestaurantRatingWithLock(review.getRestaurant().getId());

		return ReviewResponseDto.from(review);

	}

	@Transactional
	public void deleteReview(UUID reviewId, Long customerId) {
		Review review = reviewRepository.findById(reviewId).orElseThrow(()
			-> new ResourceNotFoundException("삭제 가능한 리뷰 존재하지않음"));

		if (!review.getCustomer().getId().equals(customerId)) {
			throw new IllegalArgumentException("자신의 리뷰만 삭제 가능");
		}

		review.markAsDeleted(customerId); //jpa auditing 객체를 삭제 x 아니고 누가 삭제했는지에 대한 기록
		// dirty check -> 실제로 commit()이 완료되기 전까지는 삭제됨(아님) 상태임 -> flush()
		// 커밋이 끝나면 이름표(삭제)를 떼고 실질 데이터베이스에 적용 commit()

		// 식당 Id 외래키 넘겨서 안전하게 비관적 락으로 평점/개수 갱신
		updateRestaurantRatingWithLock(review.getRestaurant().getId());

	}

	// 식당 참조 객체를 파라미터로 하지않고 외래키를 받는 이유?
	// 락을 걸겠다는 명시적 쿼리가 DB로 전송되야함
	// 객체는 findById로 이미 DB에서 영속성 컨텍스트에 얹어둠
	// select로 호출됬기 때문에 DB레벨의 Lock은 먹히지않음
	private void updateRestaurantRatingWithLock(UUID restaurantId) {
		// select .. for update 쿼리 날라가는 시점
		// db가 해당 row에 락을 쥐기때문에 동시성 제어가 가능
		Restaurant restaurant = restaurantRepository.findByIdWithPessimisticLock(restaurantId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 가게"));

		Long reviewCount = reviewRepository.countByRestaurantIdAndIsDeletedFalse(restaurant.getId());

		// 찾아온 식당의 리뷰둘의 평균 평점 계산식
		// JQPL
		double averageRating = reviewRepository.calculateAverageRatingByRestaurantId(restaurant.getId());
		restaurant.updateRatingAndCount(averageRating, reviewCount);
	}

}
