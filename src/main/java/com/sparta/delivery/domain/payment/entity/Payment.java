package com.sparta.delivery.domain.payment.entity;

import com.sparta.delivery.domain.order.entity.Order;
import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.global.common.BaseEntity;
import com.sparta.delivery.global.common.Enums;
import com.sparta.delivery.global.common.Enums.PaymentMethod;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "p_payments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE p_payments SET is_deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class Payment extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	//주문1당 결제1
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false, unique = true)
	private Order order;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id", nullable = false)
	private User customer;

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_method", nullable = false)
	private Enums.PaymentMethod paymentMethod = Enums.PaymentMethod.CARD;

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_status", nullable = false)
	private Enums.PaymentStatus paymentStatus = Enums.PaymentStatus.READY;

	@Column(nullable = false)
	private int amount;

	@Column(name = "paid_at")
	private LocalDateTime paidAt;

	@Column(name = "cancelled_at")
	private LocalDateTime cancelledAt;

	@Column(name = "card_company", length = 50)
	private String cardCompany;

	@Column(name = "card_number_masked", length = 30)
	private String cardNumberMasked;

	@Column(name = "transaction_id", length = 100)
	private String transactionId;

	private Payment(Order order, User customer, PaymentMethod paymentMethod, int amount){
		this.order = order;
		this.customer = customer;
		this.paymentMethod = paymentMethod;
		this.amount = amount;
	}

	public static Payment create(Order order, User customer, PaymentMethod paymentMethod, int amount){
		return new Payment(order, customer, paymentMethod, amount);
	}
}
