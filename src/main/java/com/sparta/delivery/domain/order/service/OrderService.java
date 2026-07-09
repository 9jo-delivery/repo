package com.sparta.delivery.domain.order.service;

import com.sparta.delivery.domain.order.dto.CancelOrderRequestDto;
import com.sparta.delivery.domain.order.dto.CreatedOrderRequestDto;
import com.sparta.delivery.domain.order.dto.OrderDetailResponseDto;
import com.sparta.delivery.domain.order.dto.OrderResponseDto;
import com.sparta.delivery.domain.order.dto.OrderSearchDto;
import com.sparta.delivery.domain.order.dto.OrderSummaryResponseDto;
import com.sparta.delivery.domain.order.dto.UpdateOrderStatusRequestDto;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    //주문 생성
    OrderResponseDto createOrder(Long customerId, CreatedOrderRequestDto request);

    //주문 목록 검색
    Page<OrderSummaryResponseDto> getOrders(Long customerId, OrderSearchDto search, Pageable pageable);

    //주문 상세 조회
    OrderDetailResponseDto getOrderDetail(Long customerId, UUID orderId);

    //주문 상태 변경
    OrderResponseDto updateOrderStatus(UUID orderId, @Valid UpdateOrderStatusRequestDto request);

    //주문 취소
    OrderResponseDto cancelOrder(Long customerId, UUID orderId, CancelOrderRequestDto request);


    //주문 삭제
}
