package com.sparta.delivery.domain.region.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sparta.delivery.domain.region.entity.Region;
import com.sparta.delivery.global.common.Enums;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class RegionSummaryResponseDto {
    private UUID id;
    private UUID parentRegionId;
    private String name;
    private Enums.RegionType regionType;

    @JsonProperty("isServiceAvailable")
    private Boolean isServiceAvailable;

    public static RegionSummaryResponseDto from(Region region) {
        return RegionSummaryResponseDto.builder()
                .id(region.getId())
                .parentRegionId(region.getParentRegion() != null
                ? region.getParentRegion().getId()
                        : null)
                .name(region.getName())
                .regionType(region.getRegionType())
                .isServiceAvailable(region.isServiceAvailable())
                .build();
    }


}