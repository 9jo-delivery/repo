package com.sparta.delivery.domain.deliveryaddress.service;

import com.sparta.delivery.domain.deliveryaddress.dto.request.DeliveryRequestDto;
import com.sparta.delivery.domain.deliveryaddress.dto.response.DeliveryDetailResponseDto;
import com.sparta.delivery.domain.deliveryaddress.dto.response.DeliveryResponseDto;
import com.sparta.delivery.domain.deliveryaddress.dto.response.DeliverySummaryDto;
import com.sparta.delivery.domain.deliveryaddress.dto.response.DeliveryUpdateResponseDto;
import com.sparta.delivery.domain.deliveryaddress.entity.DeliveryAddress;
import com.sparta.delivery.domain.deliveryaddress.repository.DeliveryAddressRepository;
import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.domain.user.repository.UserRepository;
import com.sparta.delivery.global.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
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
            -> new ResourceNotFoundException("해당 사용자를 찾을 수 없습니다."));

        if(deliveryRequestDto.getIsDefault() != null && deliveryRequestDto.getIsDefault()){
            Optional<DeliveryAddress> oldDefaultAddress = deliveryAddressRepository.findByUserAndIsDefault(user, true);

            if(oldDefaultAddress.isPresent()){
                DeliveryAddress oldAddress = oldDefaultAddress.get();
                oldAddress.updateDefault(false);
            }
        }

        DeliveryAddress deliveryAddress = DeliveryAddress.create(
                user,
                deliveryRequestDto.getAddress(),
                deliveryRequestDto.getDetailAddress(),
                deliveryRequestDto.getZipcode(),
                deliveryRequestDto.getAlias(),
                deliveryRequestDto.getIsDefault()
        );

        DeliveryAddress saveAddress = deliveryAddressRepository.save(deliveryAddress);
        return DeliveryResponseDto.from(saveAddress);
    }

    @Transactional(readOnly = true)
    public Page<DeliverySummaryDto> findAllDeliveryAddresses(Long userId, Pageable pageable) {

        Page<DeliveryAddress> addressPage = deliveryAddressRepository.findByUserId(userId, pageable);

        return addressPage.map(DeliverySummaryDto::from);
    }

    @Transactional(readOnly = true)
    public DeliveryDetailResponseDto findDeliveryAddressById(UUID addressId, Long userId) {

        DeliveryAddress address = deliveryAddressRepository.findById(addressId).orElseThrow(()
        -> new ResourceNotFoundException("배송지를 찾을 수 없습니다."));

        if(!address.getUser().getId().equals(userId)){
            throw new AccessDeniedException("본인의 배송지만 조회할 수 있습니다.");
        }

        return DeliveryDetailResponseDto.from(address);
    }

    @Transactional
    public DeliveryUpdateResponseDto updateDeliveryAddress(@Valid DeliveryRequestDto deliveryRequestDto, UUID addressId, Long userId) {
        DeliveryAddress address = deliveryAddressRepository.findById(addressId).orElseThrow(()
                -> new ResourceNotFoundException("배송지를 찾을 수 없습니다."));

        if(!address.getUser().getId().equals(userId)){
            throw new AccessDeniedException("본인의 배송지만 수정할 수 있습니다.");
        }

        if(address.getIsDefault() && (deliveryRequestDto.getIsDefault() != null && !deliveryRequestDto.getIsDefault())){
            throw new IllegalStateException("기본 배송지는 단독으로 해제할 수 없습니다. 다른 주소를 기본 배송지로 지정해 주세요");
        }

        if(deliveryRequestDto.getIsDefault() != null && deliveryRequestDto.getIsDefault()){
            Optional<DeliveryAddress> oldDefaultAddress = deliveryAddressRepository.findByUserAndIsDefault(address.getUser(), true);

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

        return buildUpdateResponse(deliveryRequestDto, changed);
    }

    @Transactional
    public void deleteDeliveryAddress(UUID addressId, Long userId) {
        DeliveryAddress address = deliveryAddressRepository.findById(addressId).orElseThrow(()
                -> new ResourceNotFoundException("배송지를 찾을 수 없습니다."));

        if(!address.getUser().getId().equals(userId)){
            throw new AccessDeniedException("본인의 배송지만 삭제할 수 있습니다.");
        }

        boolean wasDefault = address.getIsDefault();
        if(wasDefault){
            address.updateDefault(false);
        }
        address.markAsDeleted(userId);

        if(wasDefault){
            Optional<DeliveryAddress> latestAddressOpt = deliveryAddressRepository.findFirstByUserOrderByCreatedAtDesc(address.getUser());

            if(latestAddressOpt.isPresent()){
                DeliveryAddress latestAddress = latestAddressOpt.get();
                latestAddress.updateDefault(true);
            }
        }
    }

    private DeliveryUpdateResponseDto buildUpdateResponse(DeliveryRequestDto dto, List<String> changed){
        DeliveryUpdateResponseDto.DeliveryUpdateResponseDtoBuilder builder = DeliveryUpdateResponseDto.builder();

        if (changed.contains("address")) {
            builder.address(dto.getAddress());
        }
        if (changed.contains("detailAddress")) {
            builder.detailAddress(dto.getDetailAddress());
        }
        if (changed.contains("zipcode")) {
            builder.zipCode(dto.getZipcode());
        }
        if (changed.contains("alias")) {
            builder.alias(dto.getAlias());
        }
        if (changed.contains("isDefault")) {
            builder.isDefault(dto.getIsDefault());
        }
        return builder.build();
    }
}
