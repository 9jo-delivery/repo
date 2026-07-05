package com.sparta.delivery.domain.menu.entity;

import com.sparta.delivery.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "p_menu_options")
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE p_menu_options SET is_deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
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
    private Integer extraPrice = 0;

    @Column(name = "is_sold_out", nullable = false)
    private boolean isSoldOut = false;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;
}
