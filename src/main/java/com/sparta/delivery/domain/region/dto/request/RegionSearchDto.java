package com.sparta.delivery.domain.region.dto.request;

import com.sparta.delivery.global.common.Enums;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class RegionSearchDto {

    private UUID parentRegionId;
    private String name;
    private Enums.RegionType regionType;
    private Boolean isServiceAvailable;
}
