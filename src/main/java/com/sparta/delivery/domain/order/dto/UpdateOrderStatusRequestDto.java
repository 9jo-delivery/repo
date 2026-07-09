package com.sparta.delivery.domain.order.dto;

import com.sparta.delivery.global.common.Enums;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateOrderStatusRequestDto {

    @NotNull(message = "변경할 주문 상태는 필수입니다.")
    private Enums.OrderStatus orderStatus;

}
