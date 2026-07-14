package com.sparta.delivery.domain.menu.entity;

import com.sparta.delivery.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "p_menu_option_groups")
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("is_deleted = false")
public class MenuOptionGroup extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false, columnDefinition = "UUID")
    private Menu menu;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "is_required", nullable = false)
    private boolean isRequired = false;

    @Column(name = "min_select", nullable = false)
    private Integer minSelect = 0;

    @Column(name = "max_select", nullable = false)
    private Integer maxSelect = 1;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    public void update(String name, Boolean isRequired, Integer minSelect, Integer maxSelect, Integer sortOrder) {
        this.name = name;
        this.isRequired = isRequired;
        this.minSelect = minSelect;
        this.maxSelect = maxSelect;
        this.sortOrder = sortOrder;
    }
}
