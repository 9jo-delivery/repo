package com.sparta.delivery.domain.restaurant.repository;

import com.sparta.delivery.domain.restaurant.entity.RestaurantCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;


public interface RestaurantCategoryRepository extends JpaRepository<RestaurantCategory, UUID> {
    Optional<RestaurantCategory> findByName(String name);
}
