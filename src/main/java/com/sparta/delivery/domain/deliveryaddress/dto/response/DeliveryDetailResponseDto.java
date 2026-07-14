package com.sparta.delivery.domain.deliveryaddress.dto.response;

import com.sparta.delivery.domain.deliveryaddress.entity.DeliveryAddress;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class DeliveryDetailResponseDto {
    private UUID id;
    private String address;
    private String detailAddress;
    private Boolean isDefault;

    public static DeliveryDetailResponseDto from(DeliveryAddress address) {
        return DeliveryDetailResponseDto.builder()
                .id(address.getId())
                .address(address.getAddress())
                .detailAddress(address.getDetailAddress())
                .isDefault(address.getIsDefault())
                .build();
    }
}
