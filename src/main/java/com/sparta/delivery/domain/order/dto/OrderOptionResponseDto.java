package com.sparta.delivery.domain.order.dto;

import com.sparta.delivery.domain.order.entity.OrderItemOption;
import lombok.Getter;

@Getter
public class OrderOptionResponseDto {

    private final String optionGroupName;
    private final String optionName;
    private final int extraPrice;

    private OrderOptionResponseDto(String optionGroupName, String optionName, int extraPrice) {
        this.optionGroupName = optionGroupName;
        this.optionName = optionName;
        this.extraPrice = extraPrice;
    }

    public static OrderOptionResponseDto from(OrderItemOption orderItemOption) {
        return new OrderOptionResponseDto(
                orderItemOption.getOptionGroupName(),
                orderItemOption.getOptionName(),
                orderItemOption.getExtraPrice()
        );
    }
}