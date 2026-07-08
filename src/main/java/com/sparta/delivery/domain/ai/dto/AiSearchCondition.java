package com.sparta.delivery.domain.ai.dto;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor // final이 붙은 필드만 파라미터로 받는 생성자 <-- 맨날 햇갈림
// 기본 생성자
// queryDSL에 필요한 조건 Dto
public class AiSearchCondition {
	private Long owenrId;
	private UUID restaurantId;
	private Boolean isSuccess;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate startDate;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate endDate;

}
