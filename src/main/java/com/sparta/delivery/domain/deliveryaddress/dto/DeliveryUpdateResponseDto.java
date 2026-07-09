package com.sparta.delivery.domain.deliveryaddress.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)

public class DeliveryUpdateResponseDto {
    private String address;
    private String detailAddress;
    private String zipCode;
    private String alias;
    private Boolean isDefault;

}