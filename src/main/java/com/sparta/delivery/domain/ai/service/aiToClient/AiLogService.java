package com.sparta.delivery.domain.ai.service.aiToClient;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.delivery.domain.ai.entity.AiDescriptionLog;
import com.sparta.delivery.domain.ai.repository.AiDescriptRepository;
import com.sparta.delivery.domain.menu.entity.Menu;
import com.sparta.delivery.domain.restaurant.entity.Restaurant;
import com.sparta.delivery.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AiLogService {

	private final AiDescriptRepository logRepository;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void saveLog(User owner, Restaurant restaurant, Menu menu, String prompt, String responseTxt, boolean isSuccess, String errorMessage) {
		AiDescriptionLog log = AiDescriptionLog.builder()
			.owner(owner)
			.restaurant(restaurant)
			.menu(menu)
			.prompt(prompt)
			.requestText(prompt)
			.responseText(responseTxt)
			.isSuccess(isSuccess)
			.errorMessage(errorMessage)
			.build();

		logRepository.save(log);
	}
}
