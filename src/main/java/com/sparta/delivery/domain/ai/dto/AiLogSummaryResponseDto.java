package com.sparta.delivery.domain.ai.dto;

import com.sparta.delivery.domain.ai.entity.AiDescriptionLog;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
public class AiLogSummaryResponseDto {
	private String prompt;
	private String responseText;
	private boolean success;

	public static AiLogSummaryResponseDto from(AiDescriptionLog aiDescriptionLog) {
		return AiLogSummaryResponseDto.builder()
			.prompt(aiDescriptionLog.getPrompt())
			.responseText(aiDescriptionLog.getResponseText())
			.success(aiDescriptionLog.isSuccess())
			.build();
	}
}
