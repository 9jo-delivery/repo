package com.sparta.delivery.domain.review.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReviewRequestDto {
	private final Integer rating;
	private final String content;
}
