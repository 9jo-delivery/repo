package com.sparta.delivery.domain.payment.repository;

import com.sparta.delivery.domain.payment.entity.Payment;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByOrder_Id(UUID id);
}
