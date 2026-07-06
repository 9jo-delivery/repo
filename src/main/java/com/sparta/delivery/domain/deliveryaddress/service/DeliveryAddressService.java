package com.sparta.delivery.domain.deliveryaddress.service;

import com.sparta.delivery.domain.deliveryaddress.dto.DeliveryRequestDto;
import com.sparta.delivery.domain.deliveryaddress.dto.DeliveryResponseDto;
import com.sparta.delivery.domain.deliveryaddress.entity.DeliveryAddress;
import com.sparta.delivery.domain.deliveryaddress.repository.DeliveryAddressRepository;
import com.sparta.delivery.domain.user.enitiy.User;
import com.sparta.delivery.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeliveryAddressService {
    private final DeliveryAddressRepository deliveryAddressRepository;
    private final UserRepository userRepository;

    @Transactional
    public DeliveryResponseDto createDeliveryAddress(DeliveryRequestDto deliveryRequestDto) {
        User user = userRepository.findById(deliveryRequestDto.getUserId()).orElseThrow(()
        -> new IllegalArgumentException("해당 유저가 없습니다."));

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
}
