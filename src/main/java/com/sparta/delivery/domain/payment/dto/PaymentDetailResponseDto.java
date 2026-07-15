package com.sparta.delivery.domain.payment.dto;

import com.sparta.delivery.domain.payment.entity.Payment;
import com.sparta.delivery.global.common.Enums;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;

@Getter
public class PaymentDetailResponseDto {

    private final UUID id;
    private final UUID orderId;
    private final String orderNumber;
    private final Enums.PaymentMethod paymentMethod;
    private final Enums.PaymentStatus paymentStatus;
    private final int amount;

    private final LocalDateTime paidAt;
    private final LocalDateTime cancelledAt;

    private final String cardCompany;
    private final String cardNumberMasked;
    private final String transactionId;

    private PaymentDetailResponseDto(UUID id, UUID orderId, String orderNumber, Enums.PaymentMethod paymentMethod,
                                  Enums.PaymentStatus paymentStatus, int amount, LocalDateTime paidAt,
                                  LocalDateTime cancelledAt, String cardCompany, String cardNumberMasked,
                                  String transactionId) {
        this.id = id;
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.amount = amount;
        this.paidAt = paidAt;
        this.cancelledAt = cancelledAt;
        this.cardCompany = cardCompany;
        this.cardNumberMasked = cardNumberMasked;
        this.transactionId = transactionId;
    }

    public static PaymentDetailResponseDto from(Payment payment) {
        return new PaymentDetailResponseDto(
                payment.getId(),
                payment.getOrder().getId(),
                payment.getOrder().getOrderNumber(),
                payment.getPaymentMethod(),
                payment.getPaymentStatus(),
                payment.getAmount(),
                payment.getPaidAt(),
                payment.getCancelledAt(),
                payment.getCardCompany(),
                payment.getCardNumberMasked(),
                payment.getTransactionId()
        );
    }
}