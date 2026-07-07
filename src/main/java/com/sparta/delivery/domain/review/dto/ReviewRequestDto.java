package com.sparta.delivery.domain.review.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReviewRequestDto {
	@NotNull(message = "별점은 필수 입력 사항입니다.")
	@Min(value = 1)
	@Max(value = 5)
	private final Integer rating;
	private final String content;
}
