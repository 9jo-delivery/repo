package com.sparta.delivery.domain.deliveryaddress.service;

import com.sparta.delivery.domain.deliveryaddress.dto.*;
import com.sparta.delivery.domain.deliveryaddress.entity.DeliveryAddress;
import com.sparta.delivery.domain.deliveryaddress.repository.DeliveryAddressRepository;
import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.domain.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryAddressService {
    private final DeliveryAddressRepository deliveryAddressRepository;
    private final UserRepository userRepository;

    @Transactional
    public DeliveryResponseDto createDeliveryAddress(DeliveryRequestDto deliveryRequestDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()
            -> new IllegalArgumentException("해당 유저가 없습니다."));

        if(deliveryRequestDto.getIsDefault() != null && deliveryRequestDto.getIsDefault()){
            Optional<DeliveryAddress> oldDefaultAddress = deliveryAddressRepository.findByUserAndIsDefault(user, true);

            if(oldDefaultAddress.isPresent()){
                DeliveryAddress oldAddress = oldDefaultAddress.get();
                oldAddress.updateDefault(false);
            }
        }

        DeliveryAddress deliveryAddress = DeliveryAddress.builder()
                .address(deliveryRequestDto.getAddress())
                .detailAddress(deliveryRequestDto.getDetailAddress())
                .zipCode(deliveryRequestDto.getZipcode())
                .alias(deliveryRequestDto.getAlias())
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

    @Transactional(readOnly = true)
    public Page<DeliverySummaryDto> findAllDeliveryAddresses(Long userId, DeliverySearchDto deliverySearchDto) {

        int validatedSize = deliverySearchDto.getSize();
        if(validatedSize != 10 && validatedSize != 30 && validatedSize != 50){
            validatedSize = 10;
        }

        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(deliverySearchDto.getPage(), validatedSize, sort);

        Page<DeliveryAddress> addressPage = deliveryAddressRepository.findByUserId(userId, pageable);

        return addressPage.map(address -> DeliverySummaryDto.builder()
                .id(address.getId())
                .userId(userId)
                .address(address.getAddress())
                .detailAddress((address.getDetailAddress()))
                .zipcode(address.getZipCode())
                .alias(address.getAlias())
                .isDefault(address.getIsDefault())
                .build());
    }

    @Transactional(readOnly = true)
    public DeliveryDetailResponseDto findDeliveryAddressById(UUID addressId, Long userId) {

        DeliveryAddress address = deliveryAddressRepository.findById(addressId).orElseThrow(()
        -> new IllegalArgumentException("조회하고자 하는 주소가 등록되어있지 않습니다."));

        if(!address.getUser().getId().equals(userId)){
            throw new IllegalArgumentException("본인의 배송지만 조회할 수 있습니다.");
        }

        return DeliveryDetailResponseDto.builder()
                .id(address.getId())
                .address((address.getAddress()))
                .detailAddress(address.getDetailAddress())
                .isDefault(address.getIsDefault())
                .build();
    }

    @Transactional
    public DeliveryUpdateResponseDto updateDeliveryAddress(@Valid DeliveryRequestDto deliveryRequestDto, UUID addressId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

        DeliveryAddress address = deliveryAddressRepository.findById(addressId).orElseThrow(()
                -> new IllegalArgumentException("조회하고자 하는 주소가 등록되어있지 않습니다."));

        if(!address.getUser().getId().equals(user.getId())){
            throw new IllegalArgumentException("본인의 배송지만 수정할 수 있습니다.");
        }

        if(address.getIsDefault() && (deliveryRequestDto.getIsDefault() != null && !deliveryRequestDto.getIsDefault())){
            throw new IllegalArgumentException("기본 배송지는 단독으로 해제할 수 없습니다. 다른 주소를 기본 배송지로 지정해 주세요");
        }

        if(deliveryRequestDto.getIsDefault() != null && deliveryRequestDto.getIsDefault()){
            Optional<DeliveryAddress> oldDefaultAddress = deliveryAddressRepository.findByUserAndIsDefault(user, true);

            if(oldDefaultAddress.isPresent()){
                DeliveryAddress oldAddress = oldDefaultAddress.get();
                if(!oldAddress.getId().equals(addressId)){
                    oldAddress.updateDefault(false);
                }
            }
        }

        List<String> changed = address.updateFields(deliveryRequestDto.getAddress(),
                deliveryRequestDto.getDetailAddress(), deliveryRequestDto.getZipcode(),
                deliveryRequestDto.getAlias(), deliveryRequestDto.getIsDefault());

        DeliveryUpdateResponseDto.DeliveryUpdateResponseDtoBuilder responseBuilder = DeliveryUpdateResponseDto.builder();
            if(changed.contains("address")) responseBuilder.address(deliveryRequestDto.getAddress());
            if(changed.contains("detailAddress")) responseBuilder.detailAddress(deliveryRequestDto.getDetailAddress());
            if(changed.contains("zipcode")) responseBuilder.zipCode(deliveryRequestDto.getZipcode());
            if(changed.contains("alias")) responseBuilder.alias(deliveryRequestDto.getAlias());
            if(changed.contains("isDefault")) responseBuilder.isDefault(deliveryRequestDto.getIsDefault());

        return responseBuilder.build();
    }

    @Transactional
    public void deleteDeliveryAddress(UUID addressId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()
            -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

        DeliveryAddress address = deliveryAddressRepository.findById(addressId).orElseThrow(()
                -> new IllegalArgumentException("조회하고자 하는 주소가 등록되어있지 않습니다."));

        if(!address.getUser().getId().equals(user.getId())){
            throw new IllegalArgumentException("본인의 배송지만 삭제할 수 있습니다.");
        }

        boolean wasDefault = address.getIsDefault();
        if(wasDefault){
            address.updateDefault(false);
        }
        address.markAsDeleted(userId);

        if(wasDefault){
            Optional<DeliveryAddress> latestAddressOpt = deliveryAddressRepository.findFirstByUserOrderByCreatedAtDesc(user);

            if(latestAddressOpt.isPresent()){
                DeliveryAddress latestAddress = latestAddressOpt.get();
                latestAddress.updateDefault(true);
            }
        }
    }
}
