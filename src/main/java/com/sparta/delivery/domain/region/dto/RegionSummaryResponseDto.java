package com.sparta.delivery.domain.region.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sparta.delivery.global.common.Enums;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class RegionSummaryResponseDto {
    private UUID id;
    private UUID parentRegionId;
    private String name;
    private Enums.RegionType regionType;

    @JsonProperty("isServiceAvailable")
    private Boolean isServiceAvailable;
}