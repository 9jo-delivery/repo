package com.sparta.delivery.domain.order.dto;

import com.sparta.delivery.domain.order.entity.OrderItem;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class OrderItemResponseDto {

    private final String menuName;
    private final int menuPrice;
    private final int quantity;
    private final int totalPrice;
    private final List<OrderOptionResponseDto> options;

    private OrderItemResponseDto(String menuName, int menuPrice, int quantity, int totalPrice,
                              List<OrderOptionResponseDto> options) {
        this.menuName = menuName;
        this.menuPrice = menuPrice;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.options = options;
    }

    public static OrderItemResponseDto from(OrderItem orderItem) {
        List<OrderOptionResponseDto> options = orderItem.getSelectedOptions().stream()
                .map(OrderOptionResponseDto::from)
                .collect(Collectors.toList());

        return new OrderItemResponseDto(
                orderItem.getMenuName(),
                orderItem.getMenuPrice(),
                orderItem.getQuantity(),
                orderItem.getTotalPrice(),
                options
        );
    }
}