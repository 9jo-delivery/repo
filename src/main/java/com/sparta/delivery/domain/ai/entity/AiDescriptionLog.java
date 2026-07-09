package com.sparta.delivery.domain.ai.entity;

import com.sparta.delivery.domain.menu.entity.Menu;
import com.sparta.delivery.domain.restaurant.entity.Restaurant;
import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.global.common.BaseEntity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID; // UUID 임포트 추가

@Entity
@Table(name = "p_ai_description_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AiDescriptionLog extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

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

	@Column(name = "is_success", nullable = false)
	private boolean isSuccess = true;

	@Column(columnDefinition = "TEXT")
	private String errorMessage;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

	// 3. User 엔티티와의 연관 관계로 수정
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "created_by")
//	private User createdBy;

}