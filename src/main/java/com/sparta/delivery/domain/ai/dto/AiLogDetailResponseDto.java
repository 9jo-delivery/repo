package com.sparta.delivery.domain.ai.dto;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;

@Getter
public class AiLogDetailResponseDto {
	private String prompt;
	private String requestText;
	private String responseText;
	private boolean isSuccess;
	private String errorMessage;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDateTime createdAt;
}
