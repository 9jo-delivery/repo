package com.sparta.delivery.domain.deliveryaddress.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryRequestDto {
    private Long userId;
    private String address;
    private String detailAddress;
    private String zipcode;
    private String alias;
    private Boolean isDefault;
}
