package com.sparta.delivery.domain.order.controller;

import com.sparta.delivery.domain.order.dto.CreatedOrderRequestDto;
import com.sparta.delivery.domain.order.dto.OrderResponseDto;
import com.sparta.delivery.domain.order.dto.OrderSearchDto;
import com.sparta.delivery.domain.order.dto.OrderSummaryResponseDto;
import com.sparta.delivery.domain.order.service.OrderServiceImpl;
import com.sparta.delivery.global.common.Enums.OrderStatus;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderServiceImpl orderService;

    //주문 생성
    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(
            @AuthenticationPrincipal Long customerId,
            @Valid @RequestBody CreatedOrderRequestDto request
    ){

        OrderResponseDto response = orderService.createOrder(customerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //주문 목록 검색
    @GetMapping
    public ResponseEntity<Page<OrderSummaryResponseDto>> getOrders(
            @AuthenticationPrincipal Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt, desc") String sort,
            @RequestParam(required = false) OrderStatus orderStatus,
            @RequestParam(required = false) UUID restaurantId,
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate endDate
            ){
        Pageable pageable = buildPageable(page, size, sort);
        OrderSearchDto search = new OrderSearchDto(orderStatus, restaurantId, startDate, endDate);

        Page<OrderSummaryResponseDto> responses = orderService.getOrders(customerId, search, pageable);
        return ResponseEntity.ok(responses);
    }

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

    //주문 상세 조회

    //주문 상태 변경

    //주문 취소

    //주문 삭제

}
