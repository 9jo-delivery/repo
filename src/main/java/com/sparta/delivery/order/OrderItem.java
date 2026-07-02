package com.sparta.delivery.order;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import com.sparta.delivery.menu.Menu;

@Entity
@Table(name = "order_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	private Order order;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "menu_id", nullable = false)
	private Menu menu;

	@Column(nullable = false, length = 100)
	private String menuName;

	@Column(nullable = false)
	private Integer menuPrice;

	@Column(nullable = false)
	private Integer quantity;

	@Column(nullable = false)
	private Integer totalPrice;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

	private Long createdBy;
}
