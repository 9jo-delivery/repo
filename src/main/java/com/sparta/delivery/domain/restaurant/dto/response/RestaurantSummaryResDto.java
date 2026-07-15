package com.sparta.delivery.domain.restaurant.dto.response;

import com.sparta.delivery.domain.restaurant.entity.Restaurant;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class RestaurantSummaryResDto {
    /* 관리자용
    private UUID ownerId;
    private String businessNumber;*/

    private final UUID restaurantId;
    private final UUID categoryId;
    private final UUID regionId;
    private final String name;
    private final String description;
    private final String phone;
    private final String address;
    private final String detailAddress;
    private final Boolean isOpen;
    private final Integer minOrderAmount;
    private final Integer deliveryFee;
    private final BigDecimal averageRating;
    private final Long reviewCount;


    public RestaurantSummaryResDto(Restaurant restaurant) {
        this.restaurantId = restaurant.getId();
        this.categoryId = restaurant.getCategory().getId();
        this.regionId = restaurant.getRegion().getId();
        this.name = restaurant.getName();
        this.description = restaurant.getDescription();
        this.phone = restaurant.getPhone();
        this.address = restaurant.getAddress();
        this.detailAddress = restaurant.getDetailAddress();
        this.isOpen = restaurant.getIsOpen();
        this.minOrderAmount = restaurant.getMinOrderAmount();
        this.deliveryFee = restaurant.getDeliveryFee();
        this.averageRating = restaurant.getAverageRating();
        this.reviewCount = restaurant.getReviewCount();
    }
}
