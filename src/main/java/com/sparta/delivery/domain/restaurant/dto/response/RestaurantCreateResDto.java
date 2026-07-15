package com.sparta.delivery.domain.restaurant.dto.response;

import com.sparta.delivery.domain.restaurant.entity.Restaurant;
import lombok.Getter;

import java.util.UUID;

@Getter
public class  RestaurantCreateResDto {
    private final UUID restaurantId;
    private final String name;
    private final Boolean isOpen;

    public RestaurantCreateResDto(Restaurant newRestaurant) {
        this.restaurantId = newRestaurant.getId();
        this.name = newRestaurant.getName();
        this.isOpen = newRestaurant.getIsOpen();
    }
}
