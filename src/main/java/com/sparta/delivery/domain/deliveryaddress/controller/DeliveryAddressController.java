package com.sparta.delivery.domain.deliveryaddress.controller;

import com.sparta.delivery.domain.deliveryaddress.dto.*;
import com.sparta.delivery.domain.deliveryaddress.service.DeliveryAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/delivery-addresses")
public class DeliveryAddressController {
    private final DeliveryAddressService deliveryAddressService;

    @PostMapping
    public DeliveryResponseDto createDeliveryAddress(@RequestBody DeliveryRequestDto deliveryRequestDto) {
        return deliveryAddressService.createDeliveryAddress(deliveryRequestDto);
    }

    @GetMapping
    public Page<DeliverySummaryDto> findAllDeliveryAddresses(
            @RequestParam Long userId, // 테스트용 임시 유저 ID 정보
            DeliverySearchDto deliverySearchDto) {

        return deliveryAddressService.findAllDeliveryAddresses(userId, deliverySearchDto);
    }

    @GetMapping("/{addressId}")
    public DeliveryDetailResponseDto findDeliveryAddressById(
            @PathVariable UUID addressId,
            @RequestParam Long userId){ // 테스트용 임시 유저 ID 정보) {
        return deliveryAddressService.findDeliveryAddressById(addressId, userId);
    }
}
