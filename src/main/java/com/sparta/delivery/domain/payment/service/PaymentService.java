package com.sparta.delivery.domain.payment.service;

import com.sparta.delivery.domain.payment.dto.CreatePaymentRequestDto;
import com.sparta.delivery.domain.payment.dto.PaymentResponseDto;
import jakarta.validation.Valid;

public interface PaymentService {
    //결제 생성 (Ready로 생성, 주문1개당 결제1개)
    PaymentResponseDto createPayment(Long customerId, @Valid CreatePaymentRequestDto requestDto);

    //결제 승인 처리 - 결제승인콜백

    //결제 취소 - 추후 관리자 승인필요

    //결제 환불 처리 - 자동환불과는 별개, 추후 관리자가 수동으로 처리할수있게

    //결제 상세 조회 - 본인결제만

    //내 결제 목록 조회

    //결제 삭제

}
