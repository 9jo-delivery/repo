// package com.sparta.delivery.domain.order;
//
// import jakarta.persistence.*;
// import lombok.*;
// import org.hibernate.annotations.SQLDelete;
// import org.hibernate.annotations.SQLRestriction;
//
// import java.time.LocalDateTime;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.UUID;
//
// import com.sparta.delivery.domain.order.entity.OrderItem;
// import com.sparta.delivery.domain.restaurant.entity.Restaurant;
// import com.sparta.delivery.domain.user.entity.User;
// import com.sparta.delivery.global.common.BaseEntity;
// import com.sparta.delivery.global.common.Enums;
//
// @Entity
// @Table(name = "p_orders")
// @Getter
// @NoArgsConstructor(access = AccessLevel.PROTECTED)
// @SQLDelete(sql = "UPDATE p_orders SET is_deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
// @SQLRestriction("is_deleted = false")
// public class Order extends BaseEntity {
//
// 	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
// 	private UUID id;
//
// 	@ManyToOne(fetch = FetchType.LAZY)
// 	@JoinColumn(name = "customer_id", nullable = false)
// 	private User customer;
//
// 	@ManyToOne(fetch = FetchType.LAZY)
// 	@JoinColumn(name = "restaurant_id", nullable = false)
// 	private Restaurant restaurant;
//
// 	// 양방향 매핑 (필요시)
// 	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
// 	private List<OrderItem> orderItems = new ArrayList<>();
//
// 	@Column(nullable = false, unique = true, length = 100)
// 	private String orderNumber;
//
// 	@Enumerated(EnumType.STRING)
// 	@Column(nullable = false)
// 	private Enums.OrderType orderType = Enums.OrderType.ONLINE;
//
// 	@Enumerated(EnumType.STRING)
// 	@Column(nullable = false)
// 	private Enums.OrderStatus orderStatus = Enums.OrderStatus.CREATED;
//
// 	@Column(nullable = false)
// 	private Integer totalPrice;
//
// 	@Column(nullable = false)
// 	private String deliveryAddress;
//
// 	private String deliveryDetailAddress;
//
// 	@Column(length = 20)
// 	private String deliveryZipCode;
//
// 	@Column(nullable = false)
// 	private LocalDateTime orderedAt;
//
// 	private LocalDateTime acceptedAt;
// 	private LocalDateTime cookedAt;
// 	private LocalDateTime deliveredAt;
// 	private LocalDateTime completedAt;
// 	private LocalDateTime cancelledAt;
// 	private String cancelReason;
//
// 	// 비즈니스 로직 예시
// 	public void changeStatus(Enums.OrderStatus status) {
// 		this.orderStatus = status;
// 		// 상태별 시간 업데이트 로직 추가 가능
// 	}
//
// 	// testCode 때문에 필요한 필드 생성자로 땡겨왔습니다.
// 	@Builder
// 	public Order(User customer, Restaurant restaurant,  Enums.OrderStatus orderStatus) {
// 		this.customer = customer;
// 		this.restaurant = restaurant;
// 		this.orderStatus = orderStatus;
// 	}
//
//
// }
