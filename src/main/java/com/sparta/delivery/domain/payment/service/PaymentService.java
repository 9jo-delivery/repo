package com.sparta.delivery.domain.payment.service;

import com.sparta.delivery.domain.payment.dto.ConfirmPaymentRequestDto;
import com.sparta.delivery.domain.payment.dto.CreatePaymentRequestDto;
import com.sparta.delivery.domain.payment.dto.PaymentDetailResponseDto;
import com.sparta.delivery.domain.payment.dto.PaymentResponseDto;
import com.sparta.delivery.domain.payment.dto.PaymentSearchConditionDto;
import com.sparta.delivery.domain.payment.dto.PaymentSummaryResponseDto;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentService {
    //결제 생성 (Ready로 생성, 주문1개당 결제1개)
    PaymentResponseDto createPayment(Long customerId, @Valid CreatePaymentRequestDto requestDto);

    //결제 승인 처리 - 결제승인콜백
    PaymentResponseDto confirmPayment(Long customerId, UUID paymentId, @Valid ConfirmPaymentRequestDto requestDto);

    //결제 실패
    PaymentResponseDto failPayment(UUID paymentId);

    //결제 취소 - 추후 관리자 승인필요
    PaymentResponseDto cancelPayment(UUID paymentId);

    //결제 환불 처리 - 자동환불과는 별개, 추후 관리자가 수동으로 처리할수있게
    PaymentResponseDto refundPayment(UUID paymentId);

    //결제 상세 조회 - 본인결제만
    PaymentDetailResponseDto getPaymentDetail(Long customerId, UUID paymentId, boolean isAdmin);

    //내 결제 목록 조회
    Page<PaymentSummaryResponseDto> getMyPayments(Long customerId, PaymentSearchConditionDto conditionDto, Pageable pageable);

    //결제 삭제
    void deletePayment(Long customerId, UUID paymentId);

}
