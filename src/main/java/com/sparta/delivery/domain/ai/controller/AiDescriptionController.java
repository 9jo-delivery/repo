package com.sparta.delivery.domain.ai.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AiDescriptionController {

	private final AiDescriptionService aiDescriptionService;

	@GetMapping("/ai-description-logs")
	public ResponseEntity<Page<AiLogSummaryResponseDto>> getAiLog(@ModelAttribute AiSearchCondition condition,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size, @AuthenticationPrincipal UserDetails userDetails) {

		// 처음에 아예 Id를 통짜로 받았는데, userDetail(서버에서 검증이 끝난)에서 뽑아오는게 중요
		// 왜냐? 유저id는 노출되면 안돼고, 심지어 클라이언트에서 주는 걸 믿고 쓰면 데이터 변질 위험존재
		Long managerId = Long.parseLong(userDetails.getUsername());
		if (size !=10 && size !=30 && size !=50) {
			size = 10;
		}
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

		Page<AiLogSummaryResponseDto> response = aiDescriptionService.searchLogs(managerId,condition, pageable);

		return ResponseEntity.ok(response);
	}

	// 2. AI 요청 로그 상세 조회
	@GetMapping("/ai-description-logs/{logId}")
	public ResponseEntity<AiLogDetailResponseDto> getAiLogDetail(
		@PathVariable("logId") UUID logId) {


		return null;
	}
}




