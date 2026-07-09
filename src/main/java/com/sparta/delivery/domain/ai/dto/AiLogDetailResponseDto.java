package com.sparta.delivery.domain.ai.dto;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.sparta.delivery.domain.ai.entity.AiDescriptionLog;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AiLogDetailResponseDto {
	private String prompt;
	private String requestText;
	private String responseText;
	private boolean isSuccess;
	private String errorMessage;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDateTime createdAt;

	public static AiLogDetailResponseDto from(AiDescriptionLog aiLog) {
		return AiLogDetailResponseDto.builder()
			.prompt(aiLog.getPrompt())
			.requestText(aiLog.getRequestText())
			.responseText(aiLog.getResponseText())
			.isSuccess(aiLog.isSuccess())
			.errorMessage(aiLog.getErrorMessage())
			.createdAt(aiLog.getCreatedAt())
			.build();
	}
}
