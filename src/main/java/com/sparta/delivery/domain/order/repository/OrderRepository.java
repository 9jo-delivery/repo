package com.sparta.delivery.domain.order.repository;

import com.sparta.delivery.domain.order.entity.Order;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, UUID> {
}
