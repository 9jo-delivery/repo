package com.sparta.delivery.domain.order.dto;

import com.sparta.delivery.domain.order.entity.Order;
import com.sparta.delivery.global.common.Enums.OrderStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;

@Getter
public class OrderSummaryResponseDto {

    private final UUID id;
    private final String orderNumber;
    private final UUID restaurantId;
    private final String restaurantName;
    private final OrderStatus orderStatus;
    private final int totalPrice;
    private final LocalDateTime orderedAt;

    private OrderSummaryResponseDto(UUID id, String orderNumber, UUID restaurantId, String restaurantName,
                                    OrderStatus orderStatus, int totalPrice, LocalDateTime orderedAt){
        this.id = id;
        this.orderNumber = orderNumber;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.orderStatus = orderStatus;
        this.totalPrice = totalPrice;
        this.orderedAt = orderedAt;
    }

//    public static OrderSummaryResponseDto from(Order order){
//        return new OrderSummaryResponseDto(
//                order.getId(),
//                order.getOrderNumber(),
//                order.getRestaurant().getId(),
//                order.getRestaurant().getName(),
//                order.getOrderStatus(),
//                order.getOrderedAt()
//        );
//    }
}
