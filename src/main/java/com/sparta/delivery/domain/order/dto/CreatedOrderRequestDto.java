package com.sparta.delivery.domain.order.dto;

import com.sparta.delivery.domain.order.entity.Order;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreatedOrderRequestDto {

    private UUID restaurantId;
    private String deliveryAddress;
    private String deliveryDetailAddress;
    private String deliveryZipCode;
    private List<OrderItemRequestDto> orderItems;
}
