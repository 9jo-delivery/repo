package com.sparta.delivery.domain.review.controller;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.delivery.domain.review.dto.ReviewRequestDto;
import com.sparta.delivery.domain.review.dto.ReviewResponseDto;
import com.sparta.delivery.domain.review.entity.Review;
import com.sparta.delivery.domain.review.repository.ReviewRepository;
import com.sparta.delivery.domain.review.service.ReviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewController {
	private ReviewRepository reviewRepository;
	private ReviewService reviewService;

	// 리뷰 등록
	@PostMapping("/orders/{orderId}/reviews")
	public ResponseEntity<ReviewResponseDto> createReview(@PathVariable("orderId") UUID orderId, @RequestBody ReviewRequestDto request) {
		// 아직 @AuthenticationPrincipal UserDetailsImpl userDetails 구현 x
		// 따라서 임시 유저 생성
		Long customerId = 1L;
		ReviewResponseDto response = reviewService.createReview(orderId, request, customerId);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	// 리뷰 단건 조회
	@GetMapping("/reviews/{reviewId}")
	public ResponseEntity<ReviewResponseDto> getReview(@PathVariable("reviewId") UUID reviewId) {
		ReviewResponseDto response = reviewService.getReview(reviewId);
		return ResponseEntity.ok(response);
	}

	// 가게 별 리뷰 목록 조회  (GET /api/restaurants/{restaurantId}/reviews)
	@GetMapping("/restaurants/{restaurantId}/reviews")
	public ResponseEntity<Page<ReviewResponseDto>> getReviews(@PathVariable("restaurantId") UUID restaurantId,
		@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

		Page<ReviewResponseDto> response = reviewService.getRestaurantReviews(restaurantId, pageable);
		return ResponseEntity.ok(response);
	}

	// 리뷰 수정
	@PatchMapping("/reviews/{reviewId}")
	public ResponseEntity<ReviewResponseDto> updateReview(@PathVariable("reviewId") UUID reviewId, @RequestBody ReviewRequestDto request) {
		// 원래 이 메서드 파라미터에서도 유저값 받아서 해야하는데 일단 유저 아이디 임의로 정함
		Long customerId = 1L;
		ReviewResponseDto response = reviewService.updateReview(reviewId, request, customerId);
		return ResponseEntity.ok(response);
	}

	// 리뷰 삭제
	// @DeleteMapping("/reviews/{reviewId}")
	// public ResponseEntity<ReviewResponseDto> deleteReview(@PathVariable("reviewId") UUID reviewId) {
	//
	// }




}
