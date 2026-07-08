package com.sparta.delivery.domain.review.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sparta.delivery.domain.review.dto.ReviewSearchCondition;
import com.sparta.delivery.domain.review.entity.Review;

public interface ReviewRepositoryCustom {

	Page<Review> searchRestaurantReviews(UUID restaurantId, ReviewSearchCondition condition, Pageable pageable);
}
