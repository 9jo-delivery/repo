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
    private int minSelect = 0;

    @Column(name = "max_select", nullable = false)
    private int maxSelect = 1;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder = 0;

    @Version
    private Long version;

    public void update(String name, Boolean isRequired, Integer minSelect, Integer maxSelect, Integer sortOrder) {
        this.name = (name != null && !name.isEmpty()) ? name : this.name;
        this.isRequired = (isRequired != null) ? isRequired : this.isRequired;
        this.minSelect = (minSelect != null) ? minSelect : this.minSelect;
        this.maxSelect = (maxSelect != null) ? maxSelect : this.maxSelect;
        this.sortOrder = (sortOrder != null) ? sortOrder : this.sortOrder;
    }
}
