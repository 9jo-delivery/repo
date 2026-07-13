package com.sparta.delivery.domain.payment.service;

import com.sparta.delivery.domain.payment.dto.CreatePaymentRequestDto;
import com.sparta.delivery.domain.payment.dto.PaymentResponseDto;
import jakarta.validation.Valid;

public interface PaymentService {
    //결제 생성 (Ready로 생성, 주문1개당 결제1개)
    PaymentResponseDto createPayment(Long customerId, @Valid CreatePaymentRequestDto requestDto);

    //
}
