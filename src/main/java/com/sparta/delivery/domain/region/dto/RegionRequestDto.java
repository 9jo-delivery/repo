package com.sparta.delivery.domain.region.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sparta.delivery.global.common.Enums;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegionRequestDto {
    private UUID parentRegionId;
    private String name;
    private Enums.RegionType regionType;

    @JsonProperty("isServiceAvailable")
    private boolean isServiceAvailable;
}
