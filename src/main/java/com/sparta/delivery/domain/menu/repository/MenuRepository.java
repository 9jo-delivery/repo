package com.sparta.delivery.domain.menu.repository;

import com.sparta.delivery.domain.menu.entity.Menu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MenuRepository extends JpaRepository<Menu, UUID> {
    Page<Menu> findAllByRestaurantIdAndIsHiddenFalse(UUID restaurantId, Pageable pageable);
}
