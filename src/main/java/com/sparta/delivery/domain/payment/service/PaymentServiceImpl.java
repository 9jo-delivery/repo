package com.sparta.delivery.domain.payment.service;

import com.sparta.delivery.domain.order.entity.Order;
import com.sparta.delivery.domain.order.repository.OrderRepository;
import com.sparta.delivery.domain.payment.dto.ConfirmPaymentRequestDto;
import com.sparta.delivery.domain.payment.dto.CreatePaymentRequestDto;
import com.sparta.delivery.domain.payment.dto.PaymentDetailResponseDto;
import com.sparta.delivery.domain.payment.dto.PaymentResponseDto;
import com.sparta.delivery.domain.payment.dto.PaymentSearchConditionDto;
import com.sparta.delivery.domain.payment.dto.PaymentSummaryResponseDto;
import com.sparta.delivery.domain.payment.entity.Payment;
import com.sparta.delivery.domain.payment.repository.PaymentRepository;
import com.sparta.delivery.domain.payment.repository.PaymentSpecification;
import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.domain.user.repository.UserRepository;
import com.sparta.delivery.global.common.Enums;
import com.sparta.delivery.global.exception.ResourceNotFoundException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 사용자입니다."));

        Order order = orderRepository.findById(requestDto.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 주문입니다."));

        paymentRepository.findByOrder_Id(order.getId()).ifPresent(existing -> {
            throw new IllegalStateException("이미 결제가 생성된 주문입니다.");
        });

        Payment payment = Payment.create(order, customer, requestDto.getPaymentMethod(), order.getTotalPrice());
        Payment savedPayment = paymentRepository.save(payment);

        return PaymentResponseDto.from(savedPayment);
    }

    @Override
    @Transactional
    public PaymentResponseDto confirmPayment(Long customerId, UUID paymentId, ConfirmPaymentRequestDto requestDto) {
        Payment payment = paymentRepository.findByIdAndCustomer_IdForUpdate(paymentId, customerId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 결제입니다."));

        payment.confirm(requestDto.getCardCompany(), requestDto.getCardNumberMasked(), requestDto.getTransactionId());
        return PaymentResponseDto.from(payment);
    }

    @Override
    @Transactional
    public PaymentResponseDto failPayment(UUID paymentId) {
        Payment payment = paymentRepository.findByIdForUpdate(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 결제입니다."));

        payment.fail();

        return PaymentResponseDto.from(payment);
    }

    @Override
    @Transactional
    public PaymentResponseDto cancelPayment(UUID paymentId) {
        Payment payment = paymentRepository.findByIdForUpdate(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 결제입니다."));

        payment.cancel();

        return PaymentResponseDto.from(payment);
    }

    @Override
    @Transactional
    public PaymentResponseDto refundPayment(UUID paymentId) {
        Payment payment = paymentRepository.findByIdForUpdate(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 결제입니다."));

        payment.refund();

        return PaymentResponseDto.from(payment);
    }

    @Override
    public PaymentDetailResponseDto getPaymentDetail(Long customerId, UUID paymentId, boolean isAdmin) {
        Payment payment = isAdmin
                ? paymentRepository.findById(paymentId)
                        .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 결제입니다."))
                : paymentRepository.findWithOrderByIdAndCustomer_Id(paymentId, customerId)
                        .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 결제입니다."));

        return PaymentDetailResponseDto.from(payment);
    }

    @Override
    public Page<PaymentSummaryResponseDto> getMyPayments(Long customerId, PaymentSearchConditionDto conditionDto,
                                                         Pageable pageable) {
        Specification<Payment> spec = Specification.allOf(
                PaymentSpecification.customerIdEquals(customerId),
                PaymentSpecification.paymentStatusEquals(conditionDto.getPaymentStatus()),
                PaymentSpecification.orderIdEquals(conditionDto.getOrderId()),
                PaymentSpecification.createdAtBetween(
                        conditionDto.getStartDate(),
                        conditionDto.getEndDate()
                )
        );

        return paymentRepository.findAll(spec, pageable)
                .map(PaymentSummaryResponseDto::from);
    }

    @Override
    @Transactional
    public void deletePayment(Long customerId, UUID paymentId) {
        Payment payment = paymentRepository.findByIdAndCustomer_Id(paymentId, customerId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 결제입니다."));

        boolean isTerminalStatus = payment.getPaymentStatus() == Enums.PaymentStatus.FAILED
                || payment.getPaymentStatus() == Enums.PaymentStatus.CANCELLED
                || payment.getPaymentStatus() == Enums.PaymentStatus.REFUNDED;

        if (!isTerminalStatus) {
            throw new IllegalStateException("실패/취소/환불 상태의 결제만 삭제할 수 있습니다.");
        }

        payment.markAsDeleted(customerId);
    }
}