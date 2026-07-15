package com.sparta.delivery.domain.restaurant.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;

import java.util.UUID;

@Getter
public class RestaurantCreateReqDto {
    @NotNull
    private UUID categoryId;

    @NotNull
    private UUID regionId;

    @NotBlank
    private String name;

    private String description;

    private String phone;

    @NotBlank
    private String address;

    private String detailAddress;

    private String businessNumber;

    @NotNull
    @PositiveOrZero
    private Integer minOrderAmount;

    @NotNull
    @PositiveOrZero
    private Integer deliveryFee;
}

