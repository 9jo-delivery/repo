package com.sparta.delivery.domain.payment.service;

import com.sparta.delivery.domain.order.repository.OrderRepository;
import com.sparta.delivery.domain.payment.dto.CreatePaymentRequestDto;
import com.sparta.delivery.domain.payment.dto.PaymentResponseDto;
import com.sparta.delivery.domain.payment.repository.PaymentRepository;
import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService{

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public PaymentResponseDto createPayment(Long customerId, CreatePaymentRequestDto requestDto) {
        User customer = userRepository.
        return null;
    }
}
