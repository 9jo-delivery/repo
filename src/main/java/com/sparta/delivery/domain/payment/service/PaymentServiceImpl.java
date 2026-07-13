package com.sparta.delivery.domain.payment.service;

import com.sparta.delivery.domain.payment.dto.CreatePaymentRequestDto;
import com.sparta.delivery.domain.payment.dto.PaymentResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService{

    @Override
    public PaymentResponseDto createPayment(Long customerId, CreatePaymentRequestDto requestDto) {
        return null;
    }
}
