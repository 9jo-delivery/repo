package com.sparta.delivery.domain.restaurant.dto.request;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;

@Getter
public class RestaurantUpdateReqDto {
    /*관리자용
    private String detailAddress;
    private String businessNumber;
    private UUID categoryId;
    private UUID regionId;
    * */
    private String name;
    private String description;
    private String phone;
    private String address;
    private Boolean isOpen;
    @PositiveOrZero
    private Integer minOrderAmount;
    @PositiveOrZero
    private Integer deliveryFee;
}
