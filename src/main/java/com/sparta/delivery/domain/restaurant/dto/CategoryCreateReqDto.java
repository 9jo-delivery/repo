package com.sparta.delivery.domain.restaurant.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CategoryCreateReqDto {
    @NotBlank
    private String name;
    private String description;
    private Integer sortOrder;
}
