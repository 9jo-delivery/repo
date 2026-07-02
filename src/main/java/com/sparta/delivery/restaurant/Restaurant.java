package com.sparta.delivery.restaurant;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

import com.sparta.delivery.common.entity.BaseEntity;
import com.sparta.delivery.region.Region;
import com.sparta.delivery.user.User;

@Entity
@Table(name = "restaurants")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE restaurants SET is_deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class Restaurant extends BaseEntity {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

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

	@Column(nullable = false)
	private Integer reviewCount = 0;
}
