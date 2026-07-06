package com.sparta.delivery.domain.restaurant.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.sparta.delivery.global.common.BaseEntity;

import java.util.UUID;

@Entity
@Table(name = "p_restaurant_categories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE p_restaurant_categories SET is_deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class RestaurantCategory extends BaseEntity {

	@Id @GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false, unique = true, length = 50)
	private String name;

	private String description;

	@Column(nullable = false)
	private Integer sortOrder = 0;

	@Column(nullable = false)
	private boolean isActive = true;

	@Builder
	public RestaurantCategory(String name, String description, Integer sortOrder, boolean isActive) {
		this.name = name;
		this.description = description;
		this.sortOrder = sortOrder;
		this.isActive = isActive;
	}
}