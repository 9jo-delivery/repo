package com.sparta.delivery.domain.order.dto;

import com.sparta.delivery.domain.order.entity.Order;
import com.sparta.delivery.global.common.Enums;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Getter;

/**
 * 주문 상세 조회용 응답 DTO. 주문 항목/옵션까지 모두 포함.
 */
@Getter
public class OrderDetailResponseDto {

    private final UUID id;
    private final String orderNumber;
    private final Enums.OrderType orderType;
    private final Enums.OrderStatus orderStatus;
    private final int totalPrice;

    private final String deliveryAddress;
    private final String deliveryDetailAddress;
    private final String deliveryZipCode;

    private final LocalDateTime orderedAt;
    private final LocalDateTime acceptedAt;
    private final LocalDateTime cookedAt;
    private final LocalDateTime deliveredAt;
    private final LocalDateTime completedAt;
    private final LocalDateTime cancelledAt;
    private final String cancelReason;

    private final UUID restaurantId;
    private final String restaurantName;

    private final List<OrderItemResponseDto> orderItems;

    private OrderDetailResponseDto(UUID id, String orderNumber, Enums.OrderType orderType,
                                Enums.OrderStatus orderStatus, int totalPrice, String deliveryAddress,
                                String deliveryDetailAddress, String deliveryZipCode, LocalDateTime orderedAt,
                                LocalDateTime acceptedAt, LocalDateTime cookedAt, LocalDateTime deliveredAt,
                                LocalDateTime completedAt, LocalDateTime cancelledAt, String cancelReason,
                                UUID restaurantId, String restaurantName, List<OrderItemResponseDto> orderItems) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.orderType = orderType;
        this.orderStatus = orderStatus;
        this.totalPrice = totalPrice;
        this.deliveryAddress = deliveryAddress;
        this.deliveryDetailAddress = deliveryDetailAddress;
        this.deliveryZipCode = deliveryZipCode;
        this.orderedAt = orderedAt;
        this.acceptedAt = acceptedAt;
        this.cookedAt = cookedAt;
        this.deliveredAt = deliveredAt;
        this.completedAt = completedAt;
        this.cancelledAt = cancelledAt;
        this.cancelReason = cancelReason;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.orderItems = orderItems;
    }

    public static OrderDetailResponseDto from(Order order) {
        List<OrderItemResponseDto> items = order.getOrderItems().stream()
                .map(OrderItemResponseDto::from)
                .collect(Collectors.toList());

        return new OrderDetailResponseDto(
                order.getId(),
                order.getOrderNumber(),
                order.getOrderType(),
                order.getOrderStatus(),
                order.getTotalPrice(),
                order.getDeliveryAddress(),
                order.getDeliveryDetailAddress(),
                order.getDeliveryZipCode(),
                order.getOrderedAt(),
                order.getAcceptedAt(),
                order.getCookedAt(),
                order.getDeliveredAt(),
                order.getCompletedAt(),
                order.getCancelledAt(),
                order.getCancelReason(),
                order.getRestaurant().getId(),
                order.getRestaurant().getName(),
                items
        );
    }
}