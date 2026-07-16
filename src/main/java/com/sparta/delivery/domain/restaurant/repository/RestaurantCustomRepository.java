package com.sparta.delivery.domain.restaurant.repository;

import com.sparta.delivery.domain.restaurant.entity.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface RestaurantCustomRepository {
    Page<Restaurant> searchRestaurants(UUID categoryId, UUID regionId, Boolean isOpen, String name, Pageable pageable);
}
