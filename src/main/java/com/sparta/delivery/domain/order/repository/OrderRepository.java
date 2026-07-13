package com.sparta.delivery.domain.order.repository;

import com.sparta.delivery.domain.order.entity.Order;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderRepository extends JpaRepository<Order, UUID>, JpaSpecificationExecutor<Order> {

    Optional<Order> findByIdAndCustomer_Id(UUID id, long customerId);

}
