package com.sparta.delivery.domain.ai.dto.Gemini;

import java.util.List;

import lombok.Getter;

@Getter
public class GeminiResponseDto {
	private List<Candidate> candidates;

	@Getter
	public static class Candidate {
		private Content content;
	}

	@Getter
	public static class Content {
		private List<Part> parts;
	}

	@Getter
	public static class Part {
		private String text;
	}
}
