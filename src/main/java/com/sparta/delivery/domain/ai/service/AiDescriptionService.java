package com.sparta.delivery.domain.ai.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.delivery.domain.ai.dto.AiLogDetailResponseDto;
import com.sparta.delivery.domain.ai.dto.AiLogSummaryResponseDto;
import com.sparta.delivery.domain.ai.dto.AiSearchCondition;
import com.sparta.delivery.domain.ai.entity.AiDescriptionLog;
import com.sparta.delivery.domain.ai.repository.AiDescriptRepository;
import com.sparta.delivery.domain.menu.entity.Menu;
import com.sparta.delivery.domain.menu.repository.MenuRepository;
import com.sparta.delivery.domain.restaurant.repository.TempRestaurantRepository;
import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.domain.user.repository.UserRepository;
import com.sparta.delivery.global.common.Enums;
import com.sparta.delivery.global.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "AI Api")
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AiDescriptionService {

	private AiDescriptRepository aiDescriptRepository;
	private TempRestaurantRepository tempRestaurantRepository;
	private MenuRepository menuRepository;
	private UserRepository userRepository;

	public Page<AiLogSummaryResponseDto> searchLogs(Long managerId,AiSearchCondition condition,Pageable pageable) {

		User manager = userRepository.findById(managerId)
			.orElseThrow(()-> new ResourceNotFoundException("User with id " + managerId + " not found"));

		if (manager.getRole() != Enums.UserRole.MANAGER) {
			throw new IllegalArgumentException("권한없음");
		}
		// 첫번째 통과하면 이제 이거 페이지로 반환해야하고
		// 각 조건별(restaurantId, isSuccess, 기간(startDate~endDate)) 이거 대로 조히해야함
		// 그러면 queryDsl써야제
		Page<AiDescriptionLog> logPage = aiDescriptRepository.searchLogsByCondition(condition, pageable);
		return logPage.map(AiLogSummaryResponseDto::from);
	}

	public AiLogDetailResponseDto searchDetails(UUID logId, Long managerId) {
		return null;
	}
}

