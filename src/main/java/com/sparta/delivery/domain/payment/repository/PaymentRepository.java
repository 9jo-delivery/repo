package com.sparta.delivery.domain.payment.repository;

import com.sparta.delivery.domain.payment.entity.Payment;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PaymentRepository extends JpaRepository<Payment, UUID>, JpaSpecificationExecutor<Payment> {

    //주문1건당 결제1건, 중복결제생성 방지
    Optional<Payment> findByOrder_Id(UUID id);

    //본인여부 함께 확인
    Optional<Payment> findByIdAndCustomer_Id(UUID id, Long customerId);
}
