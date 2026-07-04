package com.sparta.delivery.domain.region.dto;

import com.sparta.delivery.global.common.Enums;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegionRequestDto {
    private UUID parentRegionId;
    private String name;
    private Enums.RegionType regionType;
    private boolean isServiceAvailable;
}
