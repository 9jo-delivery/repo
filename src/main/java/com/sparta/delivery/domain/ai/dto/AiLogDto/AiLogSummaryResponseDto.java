package com.sparta.delivery.domain.ai.dto.AiLogDto;

import com.sparta.delivery.domain.ai.entity.AiDescriptionLog;

import lombok.Builder;
import lombok.Getter;

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
