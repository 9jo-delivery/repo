package com.sparta.delivery.domain.menu.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record MenuUpdateRequest(

        @Size(max = 100, message = "메뉴 이름은 100자 이하로 입력해 주세요.")
        String name,

        String description,

        @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
        Integer price,

        Boolean isHidden,

        Boolean isSoldOut,

        Boolean aiGenerateDescription
) {
}
