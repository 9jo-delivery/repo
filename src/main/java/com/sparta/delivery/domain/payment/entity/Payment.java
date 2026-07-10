package com.sparta.delivery.domain.payment.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.global.common.BaseEntity;
import com.sparta.delivery.global.common.Enums;
import com.sparta.delivery.domain.order.entity.Order;

@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE payments SET is_deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class Payment extends BaseEntity {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false, unique = true)
	private Order order;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id", nullable = false)
	private User customer;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Enums.PaymentMethod paymentMethod = Enums.PaymentMethod.CARD;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Enums.PaymentStatus paymentStatus = Enums.PaymentStatus.READY;

	@Column(nullable = false)
	private Integer amount;

	private LocalDateTime paidAt;
	private LocalDateTime cancelledAt;

	@Column(length = 50)
	private String cardCompany;

	@Column(length = 30)
	private String cardNumberMasked;

	@Column(length = 100)
	private String transactionId;
}
