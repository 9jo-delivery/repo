package com.sparta.delivery.domain.ai.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.delivery.domain.ai.dto.AiLogDto.AiLogDetailResponseDto;
import com.sparta.delivery.domain.ai.dto.AiLogDto.AiLogSummaryResponseDto;
import com.sparta.delivery.domain.ai.dto.AiLogDto.AiSearchCondition;
import com.sparta.delivery.domain.ai.service.AiDescriptionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AiDescriptionController {

	private final AiDescriptionService aiDescriptionService;

	// 로그 전체 조회(page 별) + 조건 별(자세한건 명세서참고)
	@PreAuthorize("hasRole('MANAGER')")
	@GetMapping("/ai-description-logs")
	public ResponseEntity<Page<AiLogSummaryResponseDto>> getAiLog(@ModelAttribute AiSearchCondition condition,
		@PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
		// 파라미터로 page 받는법
		// @PageableDefault 기본값 세팅
		// pageable은 final이 붙은 수정 불가 객체
		// 10, 30, 50이 아닐경우 밑의 로직으로 다시 만들어야 문제가 안생김
		int size = pageable.getPageSize();
		if (size != 10 && size != 30 && size != 50) {
			size = 10;
			pageable = PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
		}

		Page<AiLogSummaryResponseDto> response = aiDescriptionService.searchLogs(condition, pageable);

		return ResponseEntity.ok(response);
	}

	// AI 요청 로그 상세 조회
	@GetMapping("/ai-description-logs/{logId}")
	@PreAuthorize("hasRole('MANAGER')")
	public ResponseEntity<AiLogDetailResponseDto> getAiLogDetail(
		@PathVariable("logId") UUID logId) {
		AiLogDetailResponseDto response = aiDescriptionService.searchDetails(logId);
		return ResponseEntity.ok(response);
	}
}




