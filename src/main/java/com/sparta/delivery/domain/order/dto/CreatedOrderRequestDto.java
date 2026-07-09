package com.sparta.delivery.domain.order.dto;

import com.sparta.delivery.domain.order.entity.Order;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreatedOrderRequestDto {

    @NotNull(message = "가게 ID는 필수입니다.")
    private UUID restaurantId;

    @NotBlank(message = "배송 주소는 필수입니다.")
    private String deliveryAddress;

    private String deliveryDetailAddress;

    private String deliveryZipCode;

    @NotEmpty(message = "주문 항목은 1개 이상이어야 합니다.")
    @Valid
    private List<OrderItemRequestDto> orderItems;
}
