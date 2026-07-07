package com.sparta.delivery.domain.restaurant;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.util.UUID;

import com.sparta.delivery.domain.user.enitiy.User;
import com.sparta.delivery.global.common.BaseEntity;
import com.sparta.delivery.domain.region.Region;

@Entity
@Table(name = "p_restaurants")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE p_restaurants SET is_deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class Restaurant extends BaseEntity {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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

	@Column(nullable = false)
	private Integer reviewCount = 0;

	// review testcode땜에 땡겨왔어요
	@Builder
	Restaurant(String name){
		this.name = name;
	}
}
