package com.sparta.delivery.domain.deliveryaddress.service;

import com.sparta.delivery.domain.deliveryaddress.dto.*;
import com.sparta.delivery.domain.deliveryaddress.entity.DeliveryAddress;
import com.sparta.delivery.domain.deliveryaddress.repository.DeliveryAddressRepository;
import com.sparta.delivery.domain.user.enitiy.User;
import com.sparta.delivery.domain.user.repository.UserRepository;
import com.sparta.delivery.global.common.Enums;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryAddressService {
    private final DeliveryAddressRepository deliveryAddressRepository;
    private final UserRepository userRepository;

    @Transactional
    public DeliveryResponseDto createDeliveryAddress(DeliveryRequestDto deliveryRequestDto) {
        User user = userRepository.findById(deliveryRequestDto.getUserId()).orElseThrow(()
        -> new IllegalArgumentException("해당 유저가 없습니다."));

        if(user.getRole() != Enums.UserRole.CUSTOMER){
            throw new IllegalArgumentException("주소 생성 권한이 없습니다.");
        }

        if(deliveryRequestDto.getIsDefault() != null && deliveryRequestDto.getIsDefault()){
            Optional<DeliveryAddress> oldDefaultAddress = deliveryAddressRepository.findByUserAndIsDefault(user, true);

            if(oldDefaultAddress.isPresent()){
                DeliveryAddress oldAddress = oldDefaultAddress.get();
                oldAddress.updateDefault(false);

                deliveryAddressRepository.save(oldAddress);
            }
        }

        DeliveryAddress deliveryAddress = DeliveryAddress.builder()
                .address(deliveryRequestDto.getAddress())
                .isDefault(deliveryRequestDto.getIsDefault())
                .user(user)
                .build();

        DeliveryAddress saveAddress = deliveryAddressRepository.save(deliveryAddress);
        return DeliveryResponseDto.builder()
                .id(saveAddress.getId())
                .address(saveAddress.getAddress())
                .isDefault(saveAddress.getIsDefault())
                .build();
    }

    public Page<DeliverySummaryDto> findAllDeliveryAddresses(Long userId, DeliverySearchDto deliverySearchDto) {
        User user = userRepository.findById(userId).orElseThrow(()
        -> new IllegalArgumentException("해당 유저가 없습니다."));

        if(user.getRole() != Enums.UserRole.CUSTOMER){
            throw new IllegalArgumentException("해당 기능의 권한이 없습니다.");
        }

        int validatedSize = deliverySearchDto.getSize();
        if(validatedSize != 10 && validatedSize != 30 && validatedSize != 50){
            validatedSize = 10;
        }

        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(deliverySearchDto.getPage(), validatedSize, sort);

        Page<DeliveryAddress> addressPage = deliveryAddressRepository.findByUserId(userId, pageable);

        return addressPage.map(address -> DeliverySummaryDto.builder()
                .id(address.getId())
                .userId(user.getId())
                .address(address.getAddress())
                .detailAddress((address.getDetailAddress()))
                .zipcode(address.getZipCode())
                .alias(address.getAlias())
                .isDefault(address.getIsDefault())
                .build());
    }

    public DeliveryDetailResponseDto findDeliveryAddressById(UUID addressId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new IllegalArgumentException("해당 유저가 없습니다."));

        if(user.getRole() != Enums.UserRole.CUSTOMER){
            throw new IllegalArgumentException("해당 기능의 권한이 없습니다.");
        }

        DeliveryAddress address = deliveryAddressRepository.findById(addressId).orElseThrow(()
        -> new IllegalArgumentException("조회하고자 하는 주소가 등록되어있지 않습니다."));

        if(!address.getUser().getId().equals(user.getId())){
            throw new IllegalArgumentException("본인의 배송지만 조회할 수 있습니다.");
        }

        return DeliveryDetailResponseDto.builder()
                .id(address.getId())
                .address((address.getAddress()))
                .detailAddress(address.getDetailAddress())
                .isDefault(address.getIsDefault())
                .build();
    }
}
