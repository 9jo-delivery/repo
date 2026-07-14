package com.sparta.delivery.domain.payment.dto;

import com.sparta.delivery.global.common.Enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreatePaymentRequestDto {

    @NotNull(message = "주문 ID는 필수입니다.")
    private UUID orderId;

    @NotNull(message = "결제 수단은 필수입니다.")
    private PaymentMethod paymentMethod;
}
