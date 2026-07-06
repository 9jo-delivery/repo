package com.sparta.delivery.domain.menu;

import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.sparta.delivery.domain.restaurant.Restaurant;
import com.sparta.delivery.global.common.BaseEntity;

@Entity
@Table(name = "p_menus")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE menus SET is_deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class Menu extends BaseEntity {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "restaurant_id", nullable = false)
	private Restaurant restaurant;

	@Column(nullable = false, length = 100)
	private String name;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(nullable = false)
	private Integer price;

	@Column(nullable = false)
	private boolean isHidden = false;

	@Column(nullable = false)
	private boolean isSoldOut = false;
}
