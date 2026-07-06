package com.sparta.delivery.domain.review.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.delivery.domain.restaurant.Restaurant;

public interface TempRestaurantRepository extends JpaRepository<Restaurant, UUID> {

}
