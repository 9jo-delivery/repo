package com.sparta.delivery.domain.menu.dto;

import com.sparta.delivery.domain.menu.entity.Menu;

import java.util.UUID;

public record MenuResponse(
        UUID menuId,
        String name,
        String description,
        Integer price,
        boolean isHidden,
        boolean isSoldOut
) {
    // 정적 팩토리 메서드
    public static MenuResponse from(Menu menu) {
        return new MenuResponse(
                menu.getId(),
                menu.getName(),
                menu.getDescription(),
                menu.getPrice(),
                menu.isHidden(),
                menu.isSoldOut()
        );
    }
}
