package com.sparta.delivery.domain.order.service;

import com.sparta.delivery.domain.order.dto.CreatedOrderRequestDto;
import com.sparta.delivery.domain.order.dto.OrderResponseDto;

public interface OrderService {

    //주문 생성
    OrderResponseDto createOrder(Long customerId, CreatedOrderRequestDto request);

    //주문 목록 검색

    //주문 상세 조회

    //주문 상태 변경

    //주문 취소

    //주문 삭제
}
