package com.sparta.delivery.domain.restaurant;

import com.sparta.delivery.domain.restaurant.entity.RestaurantCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface RestaurantCategoryRepository extends JpaRepository<RestaurantCategory, UUID> {

}
