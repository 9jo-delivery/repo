package com.sparta.delivery.domain.restaurant.dto;

import com.sparta.delivery.domain.restaurant.entity.RestaurantCategory;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CategorySummaryResDto {
    private UUID id;
    private String name;
    private String description;
    private Integer sortOrder;
    private boolean isActive;

    public CategorySummaryResDto(RestaurantCategory category) {
        this.id = category.getId();
        this.name = category.getName();
        this.description = category.getDescription();
        this.sortOrder = category.getSortOrder();
        this.isActive = category.isActive();
    }
}
