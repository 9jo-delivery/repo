package com.sparta.delivery.domain.menu.repository;

import com.sparta.delivery.domain.restaurant.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TempRestaurantRepository extends JpaRepository<Restaurant, UUID> {
}
