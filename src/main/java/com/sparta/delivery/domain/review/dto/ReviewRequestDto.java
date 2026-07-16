package com.sparta.delivery.domain.review.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReviewRequestDto {
	@NotNull(message = "별점은 필수 입력 사항입니다.")
	@Min(value = 1)
	@Max(value = 5)
	private final Integer rating;
	@NotBlank(message = "리뷰 내용은 공백일 수 없습니다.")
	@Size(max = 500, message = "리뷰는 최대 500자까지 작성 가능합니다.")
	private final String content;
}
