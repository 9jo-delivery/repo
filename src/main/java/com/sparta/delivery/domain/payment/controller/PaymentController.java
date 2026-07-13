package com.sparta.delivery.domain.payment.controller;

import com.sparta.delivery.domain.payment.dto.CreatePaymentRequestDto;
import com.sparta.delivery.domain.payment.dto.PaymentResponseDto;
import com.sparta.delivery.domain.payment.service.PaymentService;
import com.sparta.delivery.global.config.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    //결제 생성 - Ready
    @PostMapping
    public ResponseEntity<PaymentResponseDto> createPayment(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody CreatePaymentRequestDto requestDto
            ){
        Long customerId = userDetails.getUser().getId();
        PaymentResponseDto responseDto = paymentService.createPayment(customerId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    //결제 승인 처리 - 결제승인콜백

    //결제 취소 - 추후 관리자 승인필요
    //결제 환불 처리 - 자동환불과는 별개, 추후 관리자가 수동으로 처리할수있게
    //결제 상세 조회 - 본인결제만
    //내 결제 목록 조회
    //결제 삭제
}
