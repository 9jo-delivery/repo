package com.sparta.delivery.domain.restaurant.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategorySearchReqDto {
    private int page = 0;
    private int size = 10;
    private String name;
    private Boolean isActive;
}
