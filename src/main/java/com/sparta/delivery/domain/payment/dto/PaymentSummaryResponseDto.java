package com.sparta.delivery.domain.payment.dto;

import com.sparta.delivery.domain.payment.entity.Payment;
import com.sparta.delivery.global.common.Enums;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;

@Getter
public class PaymentSummaryResponseDto {

    private final UUID id;
    private final UUID orderId;
    private final String orderNumber;
    private final Enums.PaymentMethod paymentMethod;
    private final Enums.PaymentStatus paymentStatus;
    private final int amount;
    private final LocalDateTime createdAt;

    private PaymentSummaryResponseDto(UUID id, UUID orderId, String orderNumber, Enums.PaymentMethod paymentMethod,
                                      Enums.PaymentStatus paymentStatus, int amount, LocalDateTime createdAt) {
        this.id = id;
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    public static PaymentSummaryResponseDto from(Payment payment) {
        return new PaymentSummaryResponseDto(
                payment.getId(),
                payment.getOrder().getId(),
                payment.getOrder().getOrderNumber(),
                payment.getPaymentMethod(),
                payment.getPaymentStatus(),
                payment.getAmount(),
                payment.getCreatedAt()
        );
    }
}