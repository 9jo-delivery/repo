package com.sparta.delivery.domain.payment.repository;

import com.sparta.delivery.domain.payment.entity.Payment;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PaymentRepository extends JpaRepository<Payment, UUID>, JpaSpecificationExecutor<Payment> {

    Optional<Payment> findByOrder_Id(UUID id);

    Optional<Payment> findByIdAndCustomer_Id(UUID id, Long customerId);
}
