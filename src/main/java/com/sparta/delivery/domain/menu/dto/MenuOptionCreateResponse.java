package com.sparta.delivery.domain.menu.dto;

import com.sparta.delivery.domain.menu.entity.MenuOption;

import java.util.UUID;

public record MenuOptionCreateResponse(
        UUID optionId,
        String name,
        Integer extraPrice
) {
    public static MenuOptionCreateResponse from(MenuOption menuOption) {
        return new MenuOptionCreateResponse(
                menuOption.getId(),
                menuOption.getName(),
                menuOption.getExtraPrice()
        );
    }
}
