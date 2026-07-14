package com.sparta.delivery.domain.payment.controller;

import com.sparta.delivery.domain.payment.dto.ConfirmPaymentRequestDto;
import com.sparta.delivery.domain.payment.dto.CreatePaymentRequestDto;
import com.sparta.delivery.domain.payment.dto.PaymentDetailResponseDto;
import com.sparta.delivery.domain.payment.dto.PaymentResponseDto;
import com.sparta.delivery.domain.payment.dto.PaymentSearchConditionDto;
import com.sparta.delivery.domain.payment.dto.PaymentSummaryResponseDto;
import com.sparta.delivery.domain.payment.service.PaymentService;
import com.sparta.delivery.global.common.Enums.PaymentStatus;
import com.sparta.delivery.global.common.Enums.UserRole;
import com.sparta.delivery.global.config.security.UserDetailsImpl;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    //결제 생성 - Ready
    @PreAuthorize("hasRole('CUSTOMER')")
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
    @PreAuthorize("hasRole('CUSTOMER')")
    @PatchMapping("/{paymentId}/confirm")
    public ResponseEntity<PaymentResponseDto> confirmPayment(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID paymentId,
            @Valid @RequestBody ConfirmPaymentRequestDto requestDto
            ){
        Long customerId = userDetails.getUser().getId();
        PaymentResponseDto responseDto = paymentService.confirmPayment(customerId, paymentId, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    //결제 실패 처리 - 관리자 승인 필요
    @PreAuthorize("hasRole('MASTER')")
    @PatchMapping("/{paymentId}/fail")
    public ResponseEntity<PaymentResponseDto> failPayment(@PathVariable UUID paymentId){
        PaymentResponseDto responseDto = paymentService.failPayment(paymentId);
        return ResponseEntity.ok(responseDto);
    }

    //결제 취소 - 추후 관리자 승인필요
    @PreAuthorize("hasRole('MASTER')")
    @PatchMapping("/{paymentId}/cancel")
    public ResponseEntity<PaymentResponseDto> cancelPayment(@PathVariable UUID paymentId){
        PaymentResponseDto responseDto = paymentService.cancelPayment(paymentId);
        return ResponseEntity.ok(responseDto);
    }

    //결제 환불 처리 - 자동환불과는 별개, 추후 관리자가 수동으로 처리할수있게
    @PreAuthorize("hasRole('MASTER')")
    @PatchMapping("/{paymentId}/refund")
    public ResponseEntity<PaymentResponseDto> refundPayment(@PathVariable UUID paymentId){
        PaymentResponseDto responseDto = paymentService.refundPayment(paymentId);
        return ResponseEntity.ok(responseDto);
    }

    //결제 상세 조회 - 본인결제만
    @PreAuthorize("hasAnyRole('CUSTOMER', 'MASTER')")
    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentDetailResponseDto> getPaymentDetail(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID paymentId
    ) {
        Long customerId = userDetails.getUser().getId();
        boolean isAdmin = userDetails.getUser().getRole() == UserRole.MASTER;
        PaymentDetailResponseDto responseDto = paymentService.getPaymentDetail(customerId, paymentId, isAdmin);
        return ResponseEntity.ok(responseDto);
    }

    //내 결제 목록 조회
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping
    public ResponseEntity<Page<PaymentSummaryResponseDto>> getMyPayment(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            @RequestParam(required = false) PaymentStatus paymentStatus,
            @RequestParam(required = false) UUID orderId,
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE)LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE)LocalDate endDate
            ){
        Long customerId = userDetails.getUser().getId();
        Pageable pageable = buildPageable(page, size, sort);
        PaymentSearchConditionDto conditionDto = new PaymentSearchConditionDto(paymentStatus, orderId, startDate, endDate);

        Page<PaymentSummaryResponseDto> responses = paymentService.getMyPayments(customerId, conditionDto, pageable);
        return ResponseEntity.ok(responses);

    }

    // size는 10/30/50만 허용, 그 외 값은 10으로 고정. sort는 "필드명,asc|desc" 형식(기본 createdAt,desc)
    private Pageable buildPageable(int page, int size, String sort) {
        int validPage = Math.max(page, 0);
        int validSize = (size == 10 || size == 30 || size == 50) ? size : 10;

        String[] sortParams = sort.split(",");
        String property = (sortParams.length > 0 && !sortParams[0].isBlank()) ? sortParams[0].trim() : "createdAt";
        Sort.Direction direction = (sortParams.length > 1 && sortParams[1].trim().equalsIgnoreCase("asc"))
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        return PageRequest.of(validPage, validSize, Sort.by(direction, property));
    }

    //결제 삭제
    @PreAuthorize("hasRole('CUSTOMER')")
    @DeleteMapping("/{paymentId}")
    public ResponseEntity<Void> deletePayment(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID paymentId
    ){
        Long customerId = userDetails.getUser().getId();
        paymentService.deletePayment(customerId, paymentId);
        return ResponseEntity.noContent().build();
    }


}
