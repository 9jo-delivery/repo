package com.sparta.delivery.domain.order.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Table(name = "p_order_item_options")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class OrderItemOption{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;

    @Column(name = "menu_option_id", nullable = false)
    private UUID menuOptionId;

    @Column(name = "option_group_name", nullable = false, length = 100)
    private String optionGroupName;

    @Column(name = "option_name", nullable = false, length = 100)
    private String optionName;

    @Column(name = "extra_price", nullable = false)
    private int extraPrice;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private Long createdBy;

    private OrderItemOption(UUID menuOptionId, String optionGroupName, String optionName, int extraPrice) {
        this.menuOptionId = menuOptionId;
        this.optionGroupName = optionGroupName;
        this.optionName = optionName;
        this.extraPrice = extraPrice;
    }

    public static OrderItemOption create(UUID menuOptionId, String optionGroupName, String optionName, int extraPrice){
        return new OrderItemOption(
                menuOptionId,
                optionGroupName,
                optionName,
                extraPrice
        );
    }

    protected void setOrderItem(OrderItem orderItem){
        this.orderItem = orderItem;
    }
}
