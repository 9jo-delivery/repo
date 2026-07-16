package com.sparta.delivery.domain.menu.entity;

import com.sparta.delivery.domain.restaurant.entity.Restaurant;
import com.sparta.delivery.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "p_menus")
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("is_deleted = false") // WHERE is deleted_ai = false
public class Menu extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false, columnDefinition = "UUID")
    private Restaurant restaurant;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private int price;

    @Builder.Default
    @Column(name = "is_hidden", nullable = false)
    private boolean isHidden = false;

    @Builder.Default
    @Column(name = "is_sold_out", nullable = false)
    private boolean isSoldOut = false;

    @Version
    private Long version;

    public void update(String name, String description, Integer price, Boolean isHidden, Boolean isSoldOut) {
        this.name = (name != null && !name.isEmpty()) ? name : this.name;
        this.description = (description != null) ? description : this.description;
        this.price = (price != null) ? price : this.price;
        this.isHidden = (isHidden != null) ? isHidden : this.isHidden;
        this.isSoldOut = (isSoldOut != null) ? isSoldOut : this.isSoldOut;
    }

}
