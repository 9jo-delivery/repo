package com.sparta.delivery.domain.menu.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record MenuOptionGroupUpdateRequest(
        @Size(max = 100, message = "옵션 그룹명은 100자 이하로 입력해주세요")
        String name,

        Boolean isRequired,

        @Min(value = 0, message = "최소 선택 개수는 0개 이상이어야 합니다.")
        Integer minSelect,

        @Min(value = 1, message = "최대 선택 개수는 1개 이상이어야 합니다.")
        Integer maxSelect,

        Integer sortOrder
) {
}
