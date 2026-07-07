package com.sparta.delivery.domain.order.entity;

import com.sparta.delivery.domain.menu.entity.Menu;
import com.sparta.delivery.global.common.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_order_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	private Order order;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "menu_id", nullable = false)
	private Menu menu;

	@Column(name = "menu_name", nullable = false, length = 100)
	private String menuName;

	@Column(name = "menu_price", nullable = false)
	private int menuPrice;

	@Column(nullable = false)
	private int quantity;

	@Column(name = "total_price", nullable = false)
	private int totalPrice;

	@OneToMany(mappedBy = "orderItem", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private final List<OrderItemOption> selectedOptions = new ArrayList<>();

	private OrderItem(Menu menu, String menuName, int menuPrice, int quantity){
		this.menu = menu;
		this.menuName = menuName;
		this.menuPrice = menuPrice;
		this.quantity = quantity;
		this.totalPrice = menuPrice * quantity;
	}

	public static OrderItem create(Menu menu, int quantity){
		return new OrderItem(
				menu,
				menu.getName(),
				menu.getPrice(),
				quantity
		);
	}

	public void addOption(OrderItemOption orderItemOption){
		this.selectedOptions.add(orderItemOption);
		orderItemOption.setOrderItem(this);

		calculateTotalPrice();
	}

	// 총 가격 계산
	public void calculateTotalPrice(){
		int optionExtra = this.selectedOptions.stream().mapToInt(OrderItemOption::getExtraPrice).sum();
		this.totalPrice = (this.menuPrice + optionExtra) * this.quantity;
	}

	protected void setOrder(Order order){
		this.order = order;
	}

}
