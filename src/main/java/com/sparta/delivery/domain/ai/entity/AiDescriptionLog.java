package com.sparta.delivery.domain.ai.entity;

import com.sparta.delivery.domain.menu.entity.Menu;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import com.sparta.delivery.domain.restaurant.entity.Restaurant;
import com.sparta.delivery.domain.user.entity.User;

@Entity
@Table(name = "ai_description_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AiDescriptionLog {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_id", nullable = false)
	private User owner;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "restaurant_id", nullable = false)
	private Restaurant restaurant;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "menu_id")
	private Menu menu;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String prompt;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String requestText;

	@Column(columnDefinition = "TEXT")
	private String responseText;

	@Column(nullable = false)
	private boolean isSuccess = true;

	@Column(columnDefinition = "TEXT")
	private String errorMessage;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

	private Long createdBy;
}
