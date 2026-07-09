package com.sparta.delivery.domain.ai.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.delivery.domain.ai.dto.AiLogDetailResponseDto;
import com.sparta.delivery.domain.ai.dto.AiLogSummaryResponseDto;
import com.sparta.delivery.domain.ai.dto.AiSearchCondition;
import com.sparta.delivery.domain.ai.entity.AiDescriptionLog;
import com.sparta.delivery.domain.ai.repository.AiDescriptRepository;
import com.sparta.delivery.domain.menu.repository.MenuRepository;
import com.sparta.delivery.domain.restaurant.repository.TempRestaurantRepository;
import com.sparta.delivery.domain.user.repository.UserRepository;
import com.sparta.delivery.global.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "AI Api")
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AiDescriptionService {

	private final AiDescriptRepository aiDescriptRepository;
	private final TempRestaurantRepository tempRestaurantRepository;
	private final MenuRepository menuRepository;
	private final UserRepository userRepository;

	public Page<AiLogSummaryResponseDto> searchLogs(AiSearchCondition condition, Pageable pageable) {
		// 각 조건별(restaurantId, isSuccess, 기간(startDate~endDate)) 이거 대로 조히해야함
		Page<AiDescriptionLog> logPage = aiDescriptRepository.searchLogsByCondition(condition, pageable);
		return logPage.map(AiLogSummaryResponseDto::from);
	}

	public AiLogDetailResponseDto searchDetails(UUID logId) {
		AiDescriptionLog search = aiDescriptRepository.findById(logId).orElseThrow(()
			-> new ResourceNotFoundException("해당 Id가 존재하지않음"));
		return AiLogDetailResponseDto.from(search);
	}
}

