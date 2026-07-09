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
}
