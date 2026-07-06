package com.sparta.delivery.domain.region;

import com.sparta.delivery.global.common.Enums;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface RegionRepository extends JpaRepository<Region, UUID> {
    // 전체 지역 조회 쿼리
    @Query("SELECT r FROM Region r " +
            "WHERE (:parentRegionId IS NULL OR r.parentRegion.id = :parentRegionId) " +
            "AND (:name IS NULL OR r.name LIKE %:name%) " +
            "AND (:regionType IS NULL OR r.regionType = :regionType) " +
            "AND (:isServiceAvailable IS NULL OR r.isServiceAvailable = :isServiceAvailable) " +
            "AND r.isDeleted = false")
    Page<Region> searchRegions(
            @Param("parentRegionId") UUID parentRegionId,
            @Param("name") String name,
            @Param("regionType") Enums.RegionType regionType,
            @Param("isServiceAvailable") Boolean isServiceAvailable,
            Pageable pageable
    );

    boolean existsByParentRegionIdAndIsDeletedFalse(UUID regionId);
}
