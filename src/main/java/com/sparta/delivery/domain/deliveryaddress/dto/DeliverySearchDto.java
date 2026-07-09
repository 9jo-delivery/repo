package com.sparta.delivery.domain.deliveryaddress.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeliverySearchDto {
    private int page = 0;
    private int size = 10;

    private String address;
    private String detailAddress;

}
