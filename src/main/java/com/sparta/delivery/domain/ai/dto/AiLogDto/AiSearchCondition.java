package com.sparta.delivery.domain.ai.dto.AiLogDto;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
// queryDSL에 필요한 조건 Dto
public class AiSearchCondition {
	private Long ownerId;
	private UUID restaurantId;
	private Boolean isSuccess; // boolean은 null을 허용하지 않음
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate startDate;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate endDate;

	@Builder
	public AiSearchCondition(Long ownerId, UUID restaurantId, Boolean isSuccess, LocalDate startDate, LocalDate endDate) {
		this.ownerId = ownerId;
		this.restaurantId = restaurantId;
		this.isSuccess = isSuccess;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public static AiSearchCondition build(Long ownerId, UUID restaurantId, Boolean isSuccess, LocalDate startDate, LocalDate endDate)
	{
		return AiSearchCondition.builder()
			.ownerId(ownerId)
			.restaurantId(restaurantId)
			.isSuccess(isSuccess)
			.startDate(startDate)
			.endDate(endDate)
			.build();

	}
}
