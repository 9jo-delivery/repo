package com.sparta.delivery.domain.restaurant.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CategoryReqDto {
    @NotBlank
    private String name;

    private String description;
    private Integer sortOrder;
    private boolean isActive;

    public CategoryReqDto(String name, String description, Integer sortOrder, boolean isActive) {
        this.name = name;
        this.description = description;
        this.sortOrder = sortOrder;
        this.isActive = isActive;
    }
}
