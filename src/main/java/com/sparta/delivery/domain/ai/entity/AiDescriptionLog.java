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

	@Builder
	public AiDescriptionLog(User owner, Restaurant restaurant, Menu menu, String prompt, String requestText, String responseText
	, boolean isSuccess, String errorMessage) {
		this.owner = owner;
		this.restaurant = restaurant;
		this.menu = menu;
		this.prompt = prompt;
		this.requestText = requestText;
		this.responseText = responseText;
		this.isSuccess = isSuccess;
		this.errorMessage = errorMessage;
	}

	public static AiDescriptionLog create(User owner, Restaurant restaurant, Menu menu, String prompt, String requestText, String responseText) {
		return AiDescriptionLog.builder()
			.menu(menu)
			.owner(owner)
			.restaurant(restaurant)
			.prompt(prompt)
			.requestText(requestText)
			.responseText(responseText)
			.isSuccess(true)
			.build();
	}

	public static AiDescriptionLog createFailure(User owner, Restaurant restaurant, Menu menu, String prompt, String requestText, String responseText) {
		return AiDescriptionLog.builder()
			.menu(menu)
			.owner(owner)
			.restaurant(restaurant)
			.prompt(prompt)
			.requestText(requestText)
			.responseText(responseText)
			.isSuccess(false)
			.build();
	}

}