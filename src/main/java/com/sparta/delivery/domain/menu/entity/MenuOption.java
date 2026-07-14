package com.sparta.delivery.domain.menu.entity;

import com.sparta.delivery.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "p_menu_options")
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("is_deleted = false")
public class MenuOption extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_group_id", nullable = false, columnDefinition = "UUID")
    private MenuOptionGroup menuOptionGroup ;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "extra_price", nullable = false)
    private int extraPrice = 0;

    @Builder.Default
    @Column(name = "is_sold_out", nullable = false)
    private boolean isSoldOut = false;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder = 0;

    public void update(String name, Integer extraPrice, Boolean isSoldOut, Integer sortOrder) {
        this.name = (name != null && !name.isEmpty()) ? name : this.name;
        this.extraPrice = (extraPrice != null) ? extraPrice : this.extraPrice;
        this.isSoldOut = (isSoldOut != null) ? isSoldOut : this.isSoldOut;
        this.sortOrder = (sortOrder != null) ? sortOrder : this.sortOrder;
    }
}
