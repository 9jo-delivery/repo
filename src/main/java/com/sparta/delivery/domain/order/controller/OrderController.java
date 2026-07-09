package com.sparta.delivery.domain.order.controller;

import com.sparta.delivery.domain.order.dto.CancelOrderRequestDto;
import com.sparta.delivery.domain.order.dto.CreatedOrderRequestDto;
import com.sparta.delivery.domain.order.dto.OrderDetailResponseDto;
import com.sparta.delivery.domain.order.dto.OrderResponseDto;
import com.sparta.delivery.domain.order.dto.OrderSearchDto;
import com.sparta.delivery.domain.order.dto.OrderSummaryResponseDto;
import com.sparta.delivery.domain.order.dto.UpdateOrderStatusRequestDto;
import com.sparta.delivery.domain.order.service.OrderService;
import com.sparta.delivery.global.common.Enums.OrderStatus;
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
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    //주문 생성
    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
//            @RequestHeader("X-USER-ID") Long customerId,
            @Valid @RequestBody CreatedOrderRequestDto request
    ){
        Long customerId = userDetails.getUser().getId();
        OrderResponseDto response = orderService.createOrder(customerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //주문 목록 검색
    @GetMapping
    public ResponseEntity<Page<OrderSummaryResponseDto>> getOrders(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
//            @RequestHeader("X-USER-ID") Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            @RequestParam(required = false) OrderStatus orderStatus,
            @RequestParam(required = false) UUID restaurantId,
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate endDate
            ){
        Long customerId = userDetails.getUser().getId();
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
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailResponseDto> getOrderDetail(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID orderId
    ){
        long customerId = userDetails.getUser().getId();
        OrderDetailResponseDto response = orderService.getOrderDetail(customerId, orderId);
        return ResponseEntity.ok(response);
    }

    //주문 상태 변경
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderResponseDto> updateOrderStatus(
            @PathVariable UUID orderId,
            @Valid @RequestBody UpdateOrderStatusRequestDto request
    ){
        OrderResponseDto response = orderService.updateOrderStatus(orderId, request);
        return ResponseEntity.ok(response);
    }

    //주문 취소
    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponseDto> cancelOrder(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID orderId,
            @Valid @RequestBody CancelOrderRequestDto request
    ){
        Long customerId = userDetails.getUser().getId();
        OrderResponseDto response = orderService.cancelOrder(customerId, orderId, request);
        return ResponseEntity.ok(response);
    }

    //주문 삭제
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID orderId
    ){
        Long customerId = userDetails.getUser().getId();
        orderService.deleteOrder(customerId, orderId);
        return ResponseEntity.noContent().build();
    }
}
