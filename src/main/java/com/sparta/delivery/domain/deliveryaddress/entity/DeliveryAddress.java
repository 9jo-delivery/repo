package com.sparta.delivery.domain.deliveryaddress.entity;

import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "p_delivery_addresses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE p_delivery_addresses SET is_deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("is_deleted = false")
@AllArgsConstructor
@Builder
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

    public void updateDefault(boolean b) {
        this.isDefault = b;
    }

    public List<String> updateFields(String address, String detailAddress, String zipcode, String alias, Boolean isDefault) {
        List<String> changedFields = new ArrayList<>();

        if(address != null && !address.equals(this.address)){
            this.address = address;
            changedFields.add("address");
        }
        if(detailAddress != null && !detailAddress.equals(this.detailAddress)){
            this.detailAddress = detailAddress;
            changedFields.add("detailAddress");
        }
        if(zipcode != null && !zipcode.equals(this.zipCode)){
            this.zipCode = zipcode;
            changedFields.add("zipcode");
        }
        if(alias != null && !alias.equals(this.alias)){
            this.alias = alias;
            changedFields.add("alias");
        }
        if(isDefault != null && !isDefault.equals(this.isDefault)){
            this.isDefault = isDefault;
            changedFields.add("isDefault");
        }
        return changedFields;
    }
}
