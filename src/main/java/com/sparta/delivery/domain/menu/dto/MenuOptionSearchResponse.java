package com.sparta.delivery.domain.menu.dto;

import com.sparta.delivery.domain.menu.entity.MenuOption;

import java.util.UUID;

public record MenuOptionSearchResponse(
        UUID optionId,
        UUID optionGroupId,
        String name,
        Integer extraPrice,
        boolean isSoldOut
) {
    public static MenuOptionSearchResponse from(MenuOption menuOption) {
        return new MenuOptionSearchResponse(
                menuOption.getId(),
                menuOption.getMenuOptionGroup().getId(),
                menuOption.getName(),
                menuOption.getExtraPrice(),
                menuOption.isSoldOut()
        );
    }
}
