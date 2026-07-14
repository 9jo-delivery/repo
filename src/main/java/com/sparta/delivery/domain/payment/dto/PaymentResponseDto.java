package com.sparta.delivery.domain.payment.dto;

import com.sparta.delivery.domain.payment.entity.Payment;
import com.sparta.delivery.global.common.Enums.PaymentMethod;
import com.sparta.delivery.global.common.Enums.PaymentStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;

@Getter
public class PaymentResponseDto {

    private final UUID id;
    private final UUID orderId;
    private final PaymentMethod paymentMethod;
    private final PaymentStatus paymentStatus;
    private final int amount;
    private final LocalDateTime paidAt;

    public PaymentResponseDto(UUID id, UUID orderId, PaymentMethod paymentMethod, PaymentStatus paymentStatus,
                              int amount, LocalDateTime paidAt) {
        this.id = id;
        this.orderId = orderId;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.amount = amount;
        this.paidAt = paidAt;
    }

    public static PaymentResponseDto from(Payment payment){
        return new PaymentResponseDto(
                payment.getId(),
                payment.getOrder().getId(),
                payment.getPaymentMethod(),
                payment.getPaymentStatus(),
                payment.getAmount(),
                payment.getPaidAt()
        );
    }
}
