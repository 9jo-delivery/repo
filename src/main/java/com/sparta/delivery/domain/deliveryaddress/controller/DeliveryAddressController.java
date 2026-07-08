package com.sparta.delivery.domain.deliveryaddress.controller;

import com.sparta.delivery.domain.deliveryaddress.dto.*;
import com.sparta.delivery.domain.deliveryaddress.service.DeliveryAddressService;
import com.sparta.delivery.global.config.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/delivery-addresses")
public class DeliveryAddressController {
    private final DeliveryAddressService deliveryAddressService;

    @PostMapping
    public DeliveryResponseDto createDeliveryAddress(@Valid @RequestBody DeliveryRequestDto deliveryRequestDto,
                                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();
        return deliveryAddressService.createDeliveryAddress(deliveryRequestDto, userId);
    }

    @GetMapping
    public Page<DeliverySummaryDto> findAllDeliveryAddresses(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            DeliverySearchDto deliverySearchDto) {
        Long userId = userDetails.getUser().getId();
        return deliveryAddressService.findAllDeliveryAddresses(userId, deliverySearchDto);
    }

    @GetMapping("/{addressId}")
    public DeliveryDetailResponseDto findDeliveryAddressById(
            @PathVariable UUID addressId,
            @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long userId = userDetails.getUser().getId();
        return deliveryAddressService.findDeliveryAddressById(addressId, userId);
    }

    @PatchMapping("/{addressId}")
    public DeliveryUpdateResponseDto updateDeliveryAddress(
            @Valid @RequestBody DeliveryRequestDto deliveryRequestDto,
            @PathVariable UUID addressId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        return deliveryAddressService.updateDeliveryAddress(deliveryRequestDto, addressId, userId);
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteDeliveryAddress(@PathVariable UUID addressId,
                                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();
        deliveryAddressService.deleteDeliveryAddress(addressId, userId);

        return ResponseEntity.noContent().build();
    }
}
