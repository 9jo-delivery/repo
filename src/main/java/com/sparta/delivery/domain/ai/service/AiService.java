package com.sparta.delivery.domain.ai.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "AI Connect Api")
@Service
@RequiredArgsConstructor
// 외부 API와 통신만을 위한 서비스 이므로 @Transaction이 필요치 않음
public class AiService {

	private final RestTemplate restTemplate;
	@Value("{AQ.Ab8RN6IC1h7ZAZm8lgrS4e6CMS5ZDPHumr0c9uZ-mryeOMBFdQ}")
	private String geminiApiKey;

	private static final String GEMINI_API_URL =
		"https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=";


}
