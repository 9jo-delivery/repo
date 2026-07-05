package com.sparta.delivery.domain.review.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.sparta.delivery.domain.review.entity.Review;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewResponseDto {
	private UUID reviewid;
	private Integer rating;
	private String content;
	private LocalDateTime createdAt;

	// Entity를 DTO로 변환하는 펙토리 메서드(Clean Code)
	public static ReviewResponseDto from(Review review) {
		return ReviewResponseDto.builder()
			.reviewid(review.getId())
			.rating(review.getRating())
			.content(review.getContent())
			.createdAt(review.getCreatedAt())
			.build();
	}

}
