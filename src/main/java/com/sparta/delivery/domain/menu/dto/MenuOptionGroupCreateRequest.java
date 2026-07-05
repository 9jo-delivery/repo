package com.sparta.delivery.domain.menu.dto;

import jakarta.validation.constraints.*;

import java.util.UUID;

public record MenuOptionGroupCreateRequest(
        @NotNull(message = "메뉴 ID는 필수입니다.")
        UUID menuId,

        @NotBlank (message = "옵션 그룹명은 필수입니다.")
        @Size(max = 100, message = "옵션 그룹명은 100자 이하로 입력해주세요")
        String name,

        @NotNull(message = "필수 선택 여부는 필수입니다.")
        Boolean isRequired,

        @NotNull(message = "최소 선택 개수는 필수입니다.")
        @Min(value = 0, message = "최소 선택 개수는 0개 이상이어야 합니다.")
        Integer minSelect,

        @NotNull(message = "최대 선택 개수는 필수입니다.")
        @Min(value = 1, message = "최대 선택 개수는 1개 이상이어야 합니다.")
        Integer maxSelect,

        @NotNull(message = "정렬 순서는 필수입니다.")
        Integer sortOrder
        ) {
}
