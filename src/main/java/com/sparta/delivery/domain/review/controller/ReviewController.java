package com.sparta.delivery.domain.review.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

	@PostMapping("/orders/{orderId}/reviews")
	public ResponseEntity<ReviewResponseDto> createReview(@PathVariable UUID orderId, @RequestBody ReviewRequestDto request) {
		// 아직 @AuthenticationPrincipal UserDetailsImpl userDetails 구현 x
		// 따라서 임시 유저 생성
		Long customerId = 1L;
		ReviewResponseDto response = reviewService.createReview(orderId, request, customerId);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}



}
