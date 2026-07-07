package com.sparta.delivery.domain.restaurant.dto;

import com.sparta.delivery.domain.restaurant.entity.RestaurantCategory;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CategorySummaryResDto {
    private final UUID id;
    private final String name;
    private final String description;
    private final Integer sortOrder;
    private final Boolean isActive;

    public CategorySummaryResDto(RestaurantCategory category) {
        this.id = category.getId();
        this.name = category.getName();
        this.description = category.getDescription();
        this.sortOrder = category.getSortOrder();
        this.isActive = category.getIsActive();
    }
}
