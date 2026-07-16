package com.sparta.delivery.domain.menu.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record MenuOptionUpdateRequest(

        @Size(max = 100, message = "옵션명은 100자 이하로 입력해주세요")
        String name,

        @Min(value = 0, message = "추가 금액은 0원 이상이어야 합니다.")
        Integer extraPrice,

        Boolean isSoldOut,

        Integer sortOrder
) {
}
