package com.sparta.delivery.domain.deliveryaddress.repository;

import com.sparta.delivery.domain.deliveryaddress.entity.DeliveryAddress;
import com.sparta.delivery.domain.user.enitiy.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress, UUID> {
    Optional<DeliveryAddress> findByUserAndIsDefault(User user, boolean b);
}
