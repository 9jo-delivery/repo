package com.sparta.delivery.domain.region.dto;

import com.sparta.delivery.global.common.Enums;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class RegionResponseDto {
    private UUID id;
    private String name;
    private Enums.RegionType regionType;
}
