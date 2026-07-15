package com.sparta.delivery.domain.menu.dto;

import com.sparta.delivery.domain.menu.entity.Menu;

import java.util.UUID;

public record MenuCreateResponse(
        UUID menuId,
        String name,
        String description,
        Integer price
) {
    public static MenuCreateResponse from(Menu menu) {
        return new MenuCreateResponse(
                menu.getId(),
                menu.getName(),
                menu.getDescription(),
                menu.getPrice()
        );
    }
}
