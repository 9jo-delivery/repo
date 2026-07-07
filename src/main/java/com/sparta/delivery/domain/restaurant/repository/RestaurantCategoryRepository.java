package com.sparta.delivery.domain.restaurant.repository;

import com.sparta.delivery.domain.restaurant.entity.RestaurantCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;


public interface RestaurantCategoryRepository extends JpaRepository<RestaurantCategory, UUID> {
    Optional<RestaurantCategory> findByName(String name);

    @Query("""
            SELECT c
            FROM RestaurantCategory c
            WHERE c.name LIKE CONCAT('%', :name, '%')
            AND (:isActive IS NULL OR c.isActive = :isActive)
            """)
    Page<RestaurantCategory> searchCategories(
            @Param("name") String name,
            @Param("isActive") Boolean isActive,
            Pageable pageable
    );
}
