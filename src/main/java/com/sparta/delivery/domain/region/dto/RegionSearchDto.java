package com.sparta.delivery.domain.region.dto;

import com.sparta.delivery.global.common.Enums;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class RegionSearchDto {

    private int page = 0;
    private int size = 10;

    private UUID parentRegionId;
    private String name;
    private Enums.RegionType regionType;
    private Boolean isServiceAvailable;
}
