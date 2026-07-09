package com.sparta.delivery.domain.ai.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.delivery.domain.ai.dto.AiLogDetailResponseDto;
import com.sparta.delivery.domain.ai.dto.AiLogSummaryResponseDto;
import com.sparta.delivery.domain.ai.dto.AiSearchCondition;
import com.sparta.delivery.domain.ai.service.AiDescriptionService;
import com.sparta.delivery.global.common.Enums;
import com.sparta.delivery.global.config.security.UserDetailsImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AiDescriptionController {

	private final AiDescriptionService aiDescriptionService;

	@PreAuthorize("hasAnyAuthority('MANAGER')")
	@GetMapping("/ai-description-logs")
	public ResponseEntity<Page<AiLogSummaryResponseDto>> getAiLog(@ModelAttribute AiSearchCondition condition,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size) {

		if (size != 10 && size != 30 && size != 50) {
			size = 10;
		}

		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
		Page<AiLogSummaryResponseDto> response = aiDescriptionService.searchLogs(condition, pageable);

		return ResponseEntity.ok(response);
	}

	// 2. AI 요청 로그 상세 조회
	@GetMapping("/ai-description-logs/{logId}")
	@PreAuthorize("hasAnyAuthority('MANAGER')")
	public ResponseEntity<AiLogDetailResponseDto> getAiLogDetail(
		@PathVariable("logId") UUID logId) {
		AiLogDetailResponseDto response = aiDescriptionService.searchDetails(logId);
		return ResponseEntity.ok(response);
	}
}




