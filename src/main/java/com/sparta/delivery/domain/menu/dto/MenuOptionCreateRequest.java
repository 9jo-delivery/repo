package com.sparta.delivery.domain.menu.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MenuOptionCreateRequest(

        @NotBlank(message = "옵션명은 필수입니다.")
        @Size(max = 100, message = "옵션명은 100자 이하로 입력해주세요")
        String name,

        @NotNull(message = "추가 금액은 필수입니다.")
        @Min(value = 0, message = "추가 금액은 0원 이상이어야 합니다.")
        Integer extraPrice,

        @NotNull(message = "정렬 순서는 필수입니다.")
        Integer sortOrder

) {
}
