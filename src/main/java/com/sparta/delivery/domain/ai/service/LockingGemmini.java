package com.sparta.delivery.domain.ai.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.delivery.domain.ai.entity.AiDescriptionLog;
import com.sparta.delivery.domain.ai.repository.AiDescriptRepository;
import com.sparta.delivery.domain.restaurant.entity.Restaurant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class LockingGemmini {

	private final AiService aiService;
	private final AiDescriptRepository logRepository;
	private final RedisTemplate<String, String> redisTemplate;

	@Transactional
	public String createLockingGemmini(Long menuId, Long ownerId, String menuName) {

		String lockKey = "ai: lock: menu: " + menuId;
		Boolean isLocked = redisTemplate.opsForValue().setIfAbsent(lockKey, "LOCKED", 10, TimeUnit.SECONDS);

		if(Boolean.FALSE.equals(isLocked)) {
			throw new IllegalArgumentException("현재 제미나이가 설명 생성중.. 잠시후 다시 시도");
		}
		try {
			String responseText = aiService.generateMenuDescription(menuName);
			AiDescriptionLog log = AiDescriptionLog.create(ownerId, Restaurant.builder().build(), menuId)
		}

	}

}
