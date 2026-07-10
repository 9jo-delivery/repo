package com.sparta.delivery.domain.order.entity;


import com.sparta.delivery.domain.restaurant.entity.Restaurant;
import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.global.common.BaseEntity;
import com.sparta.delivery.global.common.Enums;
import com.sparta.delivery.global.common.Enums.OrderStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "p_orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE p_orders SET is_deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class Order extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id", nullable = false)
	private User customer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "restaurant_id", nullable = false)
	private Restaurant restaurant;

	@Column(name = "order_number", nullable = false, unique = true, length = 100)
	private String orderNumber;

	@Enumerated(EnumType.STRING)
	@Column(name = "order_type", nullable = false)
	private Enums.OrderType orderType = Enums.OrderType.ONLINE;

	@Enumerated(EnumType.STRING)
	@Column(name = "order_status", nullable = false)
	private Enums.OrderStatus orderStatus = Enums.OrderStatus.CREATED;

	@Column(name = "total_price", nullable = false)
	private int totalPrice;

	@Column(name = "delivery_address", nullable = false)
	private String deliveryAddress;

	@Column(name = "delivery_detail_address")
	private String deliveryDetailAddress;

	@Column(name = "delivery_zip_code")
	private String deliveryZipCode;

	@Column(name = "ordered_at", nullable = false)
	private LocalDateTime orderedAt;

	@Column(name = "accepted_at")
	private LocalDateTime acceptedAt;

	@Column(name = "cooked_at")
	private LocalDateTime cookedAt;

	@Column(name = "delivered_at")
	private LocalDateTime deliveredAt;

	@Column(name = "completed_at")
	private LocalDateTime completedAt;

	@Column(name = "cancelled_at")
	private LocalDateTime cancelledAt;

	@Column(name = "cancel_reason")
	private String cancelReason;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private final List<OrderItem> orderItems = new ArrayList<>();

	private Order(User customer, Restaurant restaurant, String orderNumber, String deliveryAddress,
	              String deliveryDetailAddress, String deliveryZipCode) {
		this.customer = customer;
		this.restaurant = restaurant;
		this.orderNumber = orderNumber;
		this.deliveryAddress = deliveryAddress;
		this.deliveryDetailAddress = deliveryDetailAddress;
		this.deliveryZipCode = deliveryZipCode;
		this.orderedAt = LocalDateTime.now();
	}

	public static Order create(User customer, Restaurant restaurant, String orderNumber, String deliveryAddress, String deliveryDetailAddress, String deliveryZipCode) {
		return new Order(customer, restaurant, orderNumber, deliveryAddress, deliveryDetailAddress, deliveryZipCode);
	}

	public void addItem(OrderItem orderItem){
		orderItems.add(orderItem);
		orderItem.setOrder(this);

		calculateTotalPrice();

	}

	// 총 가격 계산
	public void calculateTotalPrice(){
		this.totalPrice = orderItems.stream()
				.mapToInt(OrderItem::getTotalPrice)
				.sum();
	}

	public void accepted() {
		this.orderStatus = OrderStatus.ACCEPTED;
		this.acceptedAt = LocalDateTime.now();
	}
	public void cooked() {
		this.orderStatus = OrderStatus.COOKED;
		this.cookedAt = LocalDateTime.now();
	}
	public void delivered() {
		this.orderStatus = OrderStatus.DELIVERED;
		this.deliveredAt = LocalDateTime.now();
	}
	public void completed() {
		this.orderStatus = OrderStatus.COMPLETED;
		this.completedAt = LocalDateTime.now();
	}
	public void cancelled(String cancelReason) {
		this.orderStatus = OrderStatus.CANCELLED;
		this.cancelledAt = LocalDateTime.now();
		this.cancelReason = cancelReason;
	}
}
