package com.sparta.delivery.domain.deliveryaddress.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class DeliveryDetailResponseDto {
    private UUID id;
    private String address;
    private String detailAddress;
    private Boolean isDefault;
}
