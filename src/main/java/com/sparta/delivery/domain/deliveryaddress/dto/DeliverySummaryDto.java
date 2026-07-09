package com.sparta.delivery.domain.deliveryaddress.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor

public class DeliverySummaryDto {
    private UUID id;
    private Long userId;
    private String address;
    private String detailAddress;
    private String zipcode;
    private String alias;
    private Boolean isDefault;
}
