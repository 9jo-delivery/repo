package com.sparta.delivery.domain.menu.repository;

import com.sparta.delivery.domain.menu.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MenuRepository extends JpaRepository<Menu, UUID> {
    List<Menu> findAllByRestaurantId(UUID restaurantId);
}
