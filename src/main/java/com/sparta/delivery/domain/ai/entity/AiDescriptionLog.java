package com.sparta.delivery.domain.ai.entity;

import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.persistence.*;
import lombok.*;

import com.sparta.delivery.domain.menu.Menu;
import com.sparta.delivery.domain.restaurant.Restaurant;
import com.sparta.delivery.domain.user.enitiy.User;

@Entity
@Table(name = "p_ai_description_logs") // 수정: p_ 접두사 추가
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AiDescriptionLog {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID) // 수정: UUID 생성 전략 적용
	private UUID id; // 수정: Long -> UUID (명세서 UUID 타입 반영)

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