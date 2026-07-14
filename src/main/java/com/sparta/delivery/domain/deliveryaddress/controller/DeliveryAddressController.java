package com.sparta.delivery.domain.deliveryaddress.controller;

import com.sparta.delivery.domain.deliveryaddress.dto.request.DeliveryRequestDto;
import com.sparta.delivery.domain.deliveryaddress.dto.response.DeliveryDetailResponseDto;
import com.sparta.delivery.domain.deliveryaddress.dto.response.DeliveryResponseDto;
import com.sparta.delivery.domain.deliveryaddress.dto.response.DeliverySummaryDto;
import com.sparta.delivery.domain.deliveryaddress.dto.response.DeliveryUpdateResponseDto;
import com.sparta.delivery.domain.deliveryaddress.service.DeliveryAddressService;
import com.sparta.delivery.global.config.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/delivery-addresses")
public class DeliveryAddressController {
    private final DeliveryAddressService deliveryAddressService;

    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public ResponseEntity<DeliveryResponseDto> createDeliveryAddress(@Valid @RequestBody DeliveryRequestDto deliveryRequestDto,
                                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();
        return ResponseEntity.ok(deliveryAddressService.createDeliveryAddress(deliveryRequestDto, userId));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public ResponseEntity<Page<DeliverySummaryDto>> findAllDeliveryAddresses(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)Pageable pageable) {

        int size = pageable.getPageSize();
        if(size != 10 && size != 30 && size != 50){
            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    10,
                    pageable.getSort()
            );
        }
        Long userId = userDetails.getUser().getId();
        return ResponseEntity.ok(deliveryAddressService.findAllDeliveryAddresses(userId, pageable));
    }

    @GetMapping("/{addressId}")
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public ResponseEntity<DeliveryDetailResponseDto> findDeliveryAddressById(
            @PathVariable UUID addressId,
            @AuthenticationPrincipal UserDetailsImpl userDetails){
        Long userId = userDetails.getUser().getId();
        return ResponseEntity.ok(deliveryAddressService.findDeliveryAddressById(addressId, userId));
    }

    @PatchMapping("/{addressId}")
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public ResponseEntity<DeliveryUpdateResponseDto> updateDeliveryAddress(
            @Valid @RequestBody DeliveryRequestDto deliveryRequestDto,
            @PathVariable UUID addressId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        return ResponseEntity.ok(deliveryAddressService.updateDeliveryAddress(deliveryRequestDto, addressId, userId));
    }

    @DeleteMapping("/{addressId}")
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public ResponseEntity<Void> deleteDeliveryAddress(@PathVariable UUID addressId,
                                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();
        deliveryAddressService.deleteDeliveryAddress(addressId, userId);

        return ResponseEntity.noContent().build();
    }
}
