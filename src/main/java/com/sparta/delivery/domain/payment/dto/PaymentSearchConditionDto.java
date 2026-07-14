package com.sparta.delivery.domain.payment.dto;

import com.sparta.delivery.global.common.Enums;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentSearchConditionDto {

    private Enums.PaymentStatus paymentStatus;
    private UUID orderId;
    private LocalDate startDate;
    private LocalDate endDate;

    public PaymentSearchConditionDto(Enums.PaymentStatus paymentStatus, UUID orderId,
                                  LocalDate startDate, LocalDate endDate) {
        this.paymentStatus = paymentStatus;
        this.orderId = orderId;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
