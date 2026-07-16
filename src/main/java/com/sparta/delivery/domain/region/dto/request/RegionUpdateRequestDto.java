package com.sparta.delivery.domain.region.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegionUpdateRequestDto {

    private String name;
    private Boolean isServiceAvailable;
}