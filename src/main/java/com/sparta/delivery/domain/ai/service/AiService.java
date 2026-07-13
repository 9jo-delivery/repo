package com.sparta.delivery.domain.ai.service;

import com.sparta.delivery.domain.ai.dto.Gemini.GeminiRequestDto;
import com.sparta.delivery.domain.ai.dto.Gemini.GeminiResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j(topic = "AI Connect Api")
@Service
@RequiredArgsConstructor
// 외부 API와 통신만을 위한 서비스 이므로 @Transaction이 필요치 않음
public class AiService {

	private static final String GEMINI_API_URL =
		"https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=";
	private final RestTemplate restTemplate;
	@Value("${google.gemini.api-key}") // 개인 제미나이 키 넣으세요 + 노출되면 해킹위험있다니 application.yml에 따로 적는게 좋을듯 합니다.
	private String geminiApiKey;

	public String generateMenuDescription(String menuName) {
		String prompt = String.format("넌 지금부터 배달 앱이야. 메뉴 이름이 %s인 음식의 "
			+ "맛있어 보이는 설명을 50자 이내로 작성해: ", menuName);
		log.info("AI에게 보낼 프롬포트: {} ", prompt);

		GeminiRequestDto.Part part = new GeminiRequestDto.Part(prompt);
		GeminiRequestDto.Content content = new GeminiRequestDto.Content(List.of(part));
		GeminiRequestDto requestDto = new GeminiRequestDto(List.of(content));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<GeminiRequestDto> request = new HttpEntity<>(requestDto, headers);

		String requestUrl = GEMINI_API_URL + geminiApiKey;

		try {
			ResponseEntity<GeminiResponseDto> response =
				restTemplate.postForEntity(requestUrl, request, GeminiResponseDto.class); // 받은 데이터 == 텍스트 데이터
			// 스프링 컨버터가 어떻게 반환? -> 리스폰스dto 클래스 설계대로 반환
			// text -> Json 역직렬화
			return extractTextFromResponse(response.getBody());
		} catch (RestClientException e){
			log.error("API 호충중 에러 발생" , e.getMessage());
			throw new RuntimeException("Ai 메뉴 설명 생성 실패",e);
		}
	}

	private String extractTextFromResponse(GeminiResponseDto body) {
		if (body == null || body.getCandidates() == null || body.getCandidates().isEmpty()) {
			throw new RuntimeException("AI 응답하지 않음");
		}
		return body.getCandidates().get(0)
			.getContent()
			.getParts().get(0)
			.getText();
	}

}
