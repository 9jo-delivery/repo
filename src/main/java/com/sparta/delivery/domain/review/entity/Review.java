package com.sparta.delivery.domain.review.entity;

import java.util.UUID;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.global.common.BaseEntity;
import com.sparta.delivery.domain.order.Order;
import com.sparta.delivery.domain.restaurant.entity.Restaurant;

@Entity
@Table(name = "p_reviews")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE p_reviews SET is_deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class Review extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID) // 수정: UUID 생성 전략 적용
	private UUID id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false, unique = true)
	private Order order;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "restaurant_id", nullable = false)
	private Restaurant restaurant;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id", nullable = false)
	private User customer;

	@Column(nullable = false)
	private Integer rating;

	@Column(columnDefinition = "TEXT")
	private String content;

	@Builder
	public Review(Order order, Restaurant restaurant, User customer, Integer rating, String content) {
		this.order = order;
		this.restaurant = restaurant;
		this.customer = customer;
		this.rating = rating;
		this.content = content;
	}

	// 코드 리펙토링 (엔티티 객체 생성 하나하나 설정값 넣어서 하기 귀찮음때 사용)
	public static Review create(Order order, Integer rating, String content) {
		return Review.builder()
			.order(order)
			.restaurant(order.getRestaurant())
			.customer(order.getCustomer())
			.rating(rating)
			.content(content).
			build();
	}

	public void updateContentAndRating(String content, Integer rating) {
		this.content = content;
		this.rating = rating;
	}
}