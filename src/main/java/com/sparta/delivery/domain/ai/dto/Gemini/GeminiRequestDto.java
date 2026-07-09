package com.sparta.delivery.domain.ai.dto.Gemini;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GeminiRequestDto {
	private List<Content> contents;

	@Getter
	@AllArgsConstructor
	public static class Content {
		private List<Part> parts;
	}

	@Getter
	@AllArgsConstructor
	public static class Part {
		private String text;
	}
	// 구글 api가 원하는 요구 조건에 맞게 직렬화 dto로 변환
	// Json -> Java Obj
	// 따로 안만든 이유 ->  GeminiRequestDto 안에서만 세트
	// 하나로 묶기 위해서는 내부 정적 클래스 static class 필요
	// 응집도 높이는 방안 중 하나
}
