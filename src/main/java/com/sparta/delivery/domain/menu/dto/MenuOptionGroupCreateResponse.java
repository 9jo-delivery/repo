package com.sparta.delivery.domain.menu.dto;

import com.sparta.delivery.domain.menu.entity.MenuOptionGroup;

import java.util.UUID;

public record MenuOptionGroupCreateResponse(
        UUID optionGroupId,
        String name,
        Boolean isRequired,
        Integer minSelect,
        Integer maxSelect
) {
    public static MenuOptionGroupCreateResponse from(MenuOptionGroup menuOptionGroup) {
        return new MenuOptionGroupCreateResponse(
                menuOptionGroup.getId(),
                menuOptionGroup.getName(),
                menuOptionGroup.isRequired(),
                menuOptionGroup.getMinSelect(),
                menuOptionGroup.getMaxSelect()
        );
    }
}
