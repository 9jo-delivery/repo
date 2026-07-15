package com.sparta.delivery.domain.deliveryaddress.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DeliveryUpdateRequestDto {

    private String address;

    private String detailAddress;

    private String zipcode;

    private String alias;

    private Boolean isDefault;
}