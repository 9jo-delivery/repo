package com.sparta.delivery.domain.restaurant.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.delivery.domain.restaurant.entity.Restaurant;
public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {

}
