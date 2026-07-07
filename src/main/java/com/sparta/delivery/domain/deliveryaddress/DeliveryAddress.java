package com.sparta.delivery.domain.deliveryaddress;

import com.sparta.delivery.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import com.sparta.delivery.global.common.BaseEntity;
import java.util.UUID;

@Entity
@Table(name = "p_delivery_addresses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE p_delivery_addresses SET is_deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class DeliveryAddress extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String address; // 주소

    private String detailAddress; // 상세 주소

    @Column(length = 20)
    private String zipCode; // 우편번호

    @Column(length = 50)
    private String alias; // 배송지 별칭

    @Column(nullable = false)
    private Boolean isDefault = false;
}
