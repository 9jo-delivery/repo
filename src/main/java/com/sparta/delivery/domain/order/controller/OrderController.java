package com.sparta.delivery.domain.order.controller;

import com.sparta.delivery.domain.order.dto.CreatedOrderRequestDto;
import com.sparta.delivery.domain.order.dto.OrderResponseDto;
import com.sparta.delivery.domain.order.dto.OrderSummaryResponseDto;
import com.sparta.delivery.domain.order.service.OrderServiceImpl;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderServiceImpl orderService;

    //주문 생성
    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(
          //@AuthenticationPrincipal Long customerId,
            @Valid @RequestBody CreatedOrderRequestDto request
    ){
        Long customerId = 1L;
        OrderResponseDto response = orderService.createOrder(customerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //주문 목록 검색
//    @GetMapping
//    public ResponseEntity<List<OrderSummaryResponseDto>> getOrders(
//            //@AuthenticationPrincipal Long customerId
//    ){
//        List<OrderSummaryResponseDto> response = orderService.getOrders(customerId);
//    }
    //주문 상세 조회

    //주문 상태 변경

    //주문 취소

    //주문 삭제

}
