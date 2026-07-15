package com.sparta.delivery.domain.menu.entity;

import com.sparta.delivery.domain.restaurant.entity.Restaurant;
import com.sparta.delivery.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "p_menus")
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@SQLDelete(sql = "UPDATE p_menus SET is_deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
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
    private Integer price;

    @Column(name = "is_hidden", nullable = false)
    private boolean isHidden = false;

    @Column(name = "is_sold_out", nullable = false)
    private boolean isSoldOut = false;

    public void update(String name, String description, Integer price, boolean isHidden, boolean isSoldOut) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.isHidden = isHidden;
        this.isSoldOut = isSoldOut;
    }

}
