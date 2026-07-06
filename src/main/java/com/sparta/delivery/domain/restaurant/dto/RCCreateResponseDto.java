package com.sparta.delivery.domain.restaurant.dto;

import com.sparta.delivery.domain.restaurant.entity.RestaurantCategory;
import lombok.Getter;

import java.util.UUID;

@Getter
public class RCCreateResponseDto {
    private UUID id;
    private String name;

    public RCCreateResponseDto(RestaurantCategory savedCategory) {
        this.id = savedCategory.getId();
        this.name = savedCategory.getName();
    }
}
