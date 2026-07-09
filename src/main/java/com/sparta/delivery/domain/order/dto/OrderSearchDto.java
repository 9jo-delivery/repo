package com.sparta.delivery.domain.order.dto;

import com.sparta.delivery.global.common.Enums.OrderStatus;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderSearchDto {

    private OrderStatus orderStatus;
    private UUID restaurantId;
    private LocalDate startDate;
    private LocalDate endDate;

    public OrderSearchDto(OrderStatus orderStatus, UUID restaurantId, LocalDate startDate, LocalDate endDate){
        this.orderStatus = orderStatus;
        this.restaurantId = restaurantId;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
