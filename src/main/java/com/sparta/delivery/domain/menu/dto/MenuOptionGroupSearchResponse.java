package com.sparta.delivery.domain.menu.dto;

import com.sparta.delivery.domain.menu.entity.MenuOptionGroup;

import java.util.UUID;

public record MenuOptionGroupSearchResponse(
        UUID optionGroupId,
        UUID menuId,
        String name,
        boolean isRequired,
        Integer minSelect,
        Integer maxSelect,
        Integer sortOrder
) {
    public static MenuOptionGroupSearchResponse from(MenuOptionGroup menuOptionGroup) {
        return new MenuOptionGroupSearchResponse(
                menuOptionGroup.getId(),
                menuOptionGroup.getMenu().getId(),
                menuOptionGroup.getName(),
                menuOptionGroup.isRequired(),
                menuOptionGroup.getMinSelect(),
                menuOptionGroup.getMaxSelect(),
                menuOptionGroup.getSortOrder()
        );
    }
}
