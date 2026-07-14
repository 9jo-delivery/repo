package com.sparta.delivery.domain.deliveryaddress.dto.response;

import com.sparta.delivery.domain.deliveryaddress.entity.DeliveryAddress;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class DeliveryResponseDto {
    private UUID id;
    private String address;
    private Boolean isDefault;

    public static DeliveryResponseDto from(DeliveryAddress address) {
        return DeliveryResponseDto.builder()
                .id(address.getId())
                .address(address.getAddress())
                .isDefault(address.getIsDefault())
                .build();
    }
}
