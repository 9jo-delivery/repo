package com.sparta.delivery.domain.review.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.delivery.domain.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
	boolean existsByOrderId(UUID id);

	Page<Review> findAllByRestaurantId(UUID restaurantId, Pageable pageable);
}
