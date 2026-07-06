package com.sparta.delivery.domain.deliveryaddress.controller;

import com.sparta.delivery.domain.deliveryaddress.service.DeliveryAddressService;
import com.sparta.delivery.domain.deliveryaddress.dto.DeliveryRequestDto;
import com.sparta.delivery.domain.deliveryaddress.dto.DeliveryResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/delivery-addresses")
public class DeliveryAddressController {
    private final DeliveryAddressService deliveryAddressService;

    @PostMapping
    public DeliveryResponseDto createDeliveryAddress(@RequestBody DeliveryRequestDto deliveryRequestDto) {
        return deliveryAddressService.createDeliveryAddress(deliveryRequestDto);
    }
}
