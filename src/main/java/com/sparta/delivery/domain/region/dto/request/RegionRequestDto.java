package com.sparta.delivery.domain.region.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sparta.delivery.global.common.Enums;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class RegionRequestDto {

    private UUID parentRegionId;

    @NotBlank(message = "지역명은 필수입니다.")
    private String name;

    @NotNull
    private Enums.RegionType regionType;

    @NotNull
    @JsonProperty("isServiceAvailable")
    private Boolean isServiceAvailable;
}
