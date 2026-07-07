package com.sparta.delivery.domain.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderItemRequestDto {

    @NotNull(message = "메뉴 ID는 필수입니다.")
    private UUID menuId;

    @Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
    private int quantity;

    private List<UUID> selectedOptionIds = new ArrayList<>();
}
