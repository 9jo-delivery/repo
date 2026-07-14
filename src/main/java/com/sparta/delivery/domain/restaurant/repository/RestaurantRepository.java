package com.sparta.delivery.domain.restaurant.repository;

import com.sparta.delivery.domain.restaurant.entity.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {

    @Query("""
           SELECT r
           FROM Restaurant r
           WHERE r.name LIKE CONCAT('%', :name, '%')
           AND (:categoryId IS NULL OR r.category.id = :categoryId)
           AND (:regionId IS NULL OR r.region.id = :regionId)
           AND (:isOpen IS NULL OR r.isOpen = :isOpen)
           """)
    Page<Restaurant> searchRestaurants(
            @Param("categoryId") UUID categoryId,
            @Param("regionId") UUID regionId,
            @Param("isOpen") Boolean isOpen,
            @Param("name") String name,
            Pageable pageable
    );
}
