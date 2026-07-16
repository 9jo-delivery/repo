package com.sparta.delivery.domain.deliveryaddress.repository;

import com.sparta.delivery.domain.deliveryaddress.entity.DeliveryAddress;
import com.sparta.delivery.domain.user.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;
import java.util.UUID;

public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<DeliveryAddress> findByUserAndIsDefault(User user, boolean b);

    Page<DeliveryAddress> findByUserId(Long userId, Pageable pageable);

    Optional<DeliveryAddress> findFirstByUserOrderByCreatedAtDesc(User user); // 가장 최신에 생성한 주소 찾기
}
