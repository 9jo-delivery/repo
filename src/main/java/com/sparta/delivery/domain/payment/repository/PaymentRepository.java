package com.sparta.delivery.domain.payment.repository;

import com.sparta.delivery.domain.payment.entity.Payment;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentRepository extends JpaRepository<Payment, UUID>, JpaSpecificationExecutor<Payment> {

    //주문1건당 결제1건, 중복결제생성 방지
    Optional<Payment> findByOrder_Id(UUID id);

    //본인여부 함께 확인
    Optional<Payment> findByIdAndCustomer_Id(UUID id, Long customerId);

    @EntityGraph(attributePaths = {"order"})
    Optional<Payment> findWithOrderByIdAndCustomer_Id(UUID id, Long customerId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Payment p where p.id = :id")
    Optional<Payment> findByIdForUpdate(@Param("id") UUID id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Payment p where p.id = :id and p.customer.id = :customerId")
    Optional<Payment> findByIdAndCustomer_IdForUpdate(@Param("id") UUID id, @Param("customerId") Long customerId);
}
