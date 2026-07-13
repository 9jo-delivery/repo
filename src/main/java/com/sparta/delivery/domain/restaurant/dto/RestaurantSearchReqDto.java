package com.sparta.delivery.domain.restaurant.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class RestaurantSearchReqDto {
    private UUID categoryId;
    private UUID regionId;
    private String name;
    private Boolean isOpen;
}
