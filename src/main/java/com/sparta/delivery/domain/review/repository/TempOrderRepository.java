package com.sparta.delivery.domain.review.repository;

import com.sparta.delivery.domain.order.entity.Order;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TempOrderRepository extends JpaRepository<Order, UUID> {

	Optional<Order> findById(UUID orderId);
}
