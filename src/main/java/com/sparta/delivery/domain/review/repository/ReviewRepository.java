package com.sparta.delivery.domain.review.repository;

import com.sparta.delivery.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID>, ReviewRepositoryCustom {
	boolean existsByOrderId(UUID id);

	Page<Review> findAllByRestaurantId(UUID restaurantId, Pageable pageable);

	Long countByRestaurantIdAndIsDeletedFalse(UUID id);

	@Query("SELECT COALESCE(AVG(r.rating), 0.0) " +
		"FROM Review r " +
		"WHERE r.restaurant.id = :restaurantId " +
		"AND r.isDeleted = false")
	Double calculateAverageRatingByRestaurantId(UUID id); // Double인 이유: 리뷰가 아얘 없으면 결과가 0이 아니라 Null
	// 따라서 기존의 double은 null을 담을수 없음 -> Double은 null을 지정한 디폴트값 0.0으로 변환
}
