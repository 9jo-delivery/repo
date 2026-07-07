package com.sparta.delivery.domain.order.dto;

import com.sparta.delivery.domain.order.entity.Order;
import com.sparta.delivery.global.common.Enums.OrderStatus;
import com.sparta.delivery.global.common.Enums.OrderType;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;

@Getter
public class OrderResponseDto {

    private final UUID id;
    private final String orderNumber;
    private final OrderType orderType;
    private final OrderStatus orderStatus;
    private final int totalPrice;
    private final LocalDateTime orderedAt;

    private OrderResponseDto(UUID id, String orderNumber, OrderType orderType, OrderStatus orderStatus, int totalPrice, LocalDateTime orderedAt){
        this.id = id;
        this.orderNumber = orderNumber;
        this.orderType = orderType;
        this.orderStatus = orderStatus;
        this.totalPrice = totalPrice;
        this.orderedAt = orderedAt;
    }

    public static OrderResponseDto from(Order order){
        return new OrderResponseDto(
                order.getId(),
                order.getOrderNumber(),
                order.getOrderType(),
                order.getOrderStatus(),
                order.getTotalPrice(),
                order.getOrderedAt()
        );
    }

}
