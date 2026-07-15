package com.sparta.delivery.domain.region.dto.response;

import com.sparta.delivery.domain.region.entity.Region;
import com.sparta.delivery.global.common.Enums;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class RegionResponseDto {
    private UUID id;
    private String name;
    private Enums.RegionType regionType;

    public static RegionResponseDto from(Region region) {
        return RegionResponseDto.builder()
                .id(region.getId())
                .name(region.getName())
                .regionType(region.getRegionType())
                .build();
    }
}
