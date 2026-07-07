package com.sparta.delivery.domain.menu.repository;


import com.sparta.delivery.domain.restaurant.entity.Restaurant;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {
}
