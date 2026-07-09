package com.sparta.delivery.domain.ai.dto.AiLogDto;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor // 기본생성자
@NoArgsConstructor // 테스트코드 위해서 필드값 모조리 넣는데 쓰는 생성자
// queryDSL에 필요한 조건 Dto
public class AiSearchCondition {
	private Long ownerId;
	private UUID restaurantId;
	private Boolean isSuccess; // boolean은 null을 허용하지 않음
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate startDate;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate endDate;

}
