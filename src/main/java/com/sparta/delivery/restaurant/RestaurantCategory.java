package com.sparta.delivery.restaurant;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.sparta.delivery.common.entity.BaseEntity;

@Entity
@Table(name = "restaurant_categories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE restaurant_categories SET is_deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class RestaurantCategory extends BaseEntity {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 50)
	private String name;

	private String description;

	@Column(nullable = false)
	private Integer sortOrder = 0;

	@Column(nullable = false)
	private boolean isActive = true;
}