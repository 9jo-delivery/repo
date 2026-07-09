package com.sparta.delivery.domain.restaurant.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.util.UUID;

import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.global.common.BaseEntity;
import com.sparta.delivery.domain.region.entity.Region;

@Entity
@Table(name = "p_restaurants")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE p_restaurants SET is_deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class Restaurant extends BaseEntity {

	@Id @GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_id", nullable = false)
	private User owner;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id", nullable = false)
	private RestaurantCategory category;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "region_id", nullable = false)
	private Region region;

	@Column(nullable = false, length = 100)
	private String name;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(length = 30)
	private String phone;

	@Column(nullable = false)
	private String address;

	private String detailAddress;

	@Column(length = 50)
	private String businessNumber;

	@Column(nullable = false)
	private boolean isOpen = true;

	@Column(nullable = false)
	private Integer minOrderAmount = 0;

	@Column(nullable = false)
	private Integer deliveryFee = 0;

	@Column(nullable = false, precision = 2, scale = 1)
	private BigDecimal averageRating = BigDecimal.ZERO;

	@Column(nullable = false, columnDefinition = "bigint default 0")
	private Long reviewCount = 0L; // count()함수쓸때 Long타입 반환만 가능해서 수정했습니다.

	public static Restaurant create(
			User owner,
			RestaurantCategory category,
			Region region,
			String name,
			String description,
			String phone,
			String address,
			String detailAddress,
			String businessNumber,
			Integer minOrderAmount,
			Integer deliveryFee
	) {
		Restaurant restaurant = new Restaurant();
		restaurant.owner = owner;
		restaurant.category = category;
		restaurant.region = region;
		restaurant.name = name;
		restaurant.description = description;
		restaurant.phone = phone;
		restaurant.address = address;
		restaurant.detailAddress = detailAddress;
		restaurant.businessNumber = businessNumber;
		restaurant.minOrderAmount = minOrderAmount;
		restaurant.deliveryFee = deliveryFee;
		return restaurant;
	}

	public void updateRatingAndCount(double averageRating, Long reviewCount) {
		this.averageRating = BigDecimal.valueOf(averageRating);
		this.reviewCount = reviewCount;
	}
}
