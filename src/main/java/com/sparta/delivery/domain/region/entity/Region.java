package com.sparta.delivery.domain.region.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import com.sparta.delivery.global.common.BaseEntity;
import com.sparta.delivery.global.common.Enums;
import java.util.UUID;

@Entity
@Table(name = "p_regions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE p_regions SET is_deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("is_deleted = false")
@AllArgsConstructor
@Builder
public class Region extends BaseEntity {

	@Id @GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_region_id")
	private Region parentRegion;

	@Column(nullable = false, length = 100)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Enums.RegionType regionType;

	@Column(nullable = false)
	private boolean isServiceAvailable = false;

	public void updateRegion(String name, boolean isServiceAvailable) {
		this.name = name;
		this.isServiceAvailable = isServiceAvailable;
	}
}
