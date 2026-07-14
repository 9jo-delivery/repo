package com.sparta.delivery.domain.deliveryaddress.dto.response;

import com.sparta.delivery.domain.deliveryaddress.entity.DeliveryAddress;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class DeliverySummaryDto {
    private UUID id;
    private Long userId;
    private String address;
    private String detailAddress;
    private String zipcode;
    private String alias;
    private Boolean isDefault;

    public static DeliverySummaryDto from(DeliveryAddress address){
        return DeliverySummaryDto.builder()
                .id(address.getId())
                .userId(address.getUser().getId())
                .address(address.getAddress())
                .detailAddress(address.getDetailAddress())
                .zipcode(address.getZipCode())
                .alias(address.getAlias())
                .isDefault(address.getIsDefault())
                .build();
    }
}
