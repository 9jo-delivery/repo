package com.sparta.delivery.domain.ai.service.aiToClient;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.sparta.delivery.domain.menu.entity.Menu;
import com.sparta.delivery.domain.restaurant.entity.Restaurant;
import com.sparta.delivery.domain.user.entity.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiTotalService {

	private final AiService aiService;
	private final RedisTemplate<String, String> redisTemplate;
	private final AiLogService aiLogService;

	public String generateMenuDescript(User owner, Restaurant restaurant, Menu menu) {

		String lockKey = "ai: lock: menu: " + menu.getId();
		acquireLock(lockKey);

		String prompt = createPrompt(menu.getName());
		try {
			String responseTxt = aiService.requestToGemini(prompt);
			aiLogService.saveLog(owner, restaurant, menu, prompt, responseTxt, true, null);
			return responseTxt;
		} catch (Exception e) {
			aiLogService.saveLog(owner, restaurant, menu, prompt, null, false, e.getMessage());
			throw e;
		} finally {
			releaseLock(lockKey);
		}
	}

	private void releaseLock(String lockKey) {
		redisTemplate.delete(lockKey);
	}

	private String createPrompt(String menuName) {
		return String.format("넌 지금부터 배달 앱이야. 메뉴 이름이 %s인 음식의 맛있어 보이는 설명을 50자 이내로 작성해", menuName);
	}

	private void acquireLock(String lockKey) {
		Boolean isLocked = redisTemplate.opsForValue().setIfAbsent(lockKey, "LOCKED", 15, TimeUnit.SECONDS);
		if (Boolean.FALSE.equals(isLocked)) {
			throw new IllegalArgumentException("현재 메뉴에 대한 AI 설명 생성중");
		}
	}

}
