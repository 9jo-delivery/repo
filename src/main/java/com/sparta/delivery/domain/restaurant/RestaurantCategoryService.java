package com.sparta.delivery.domain.restaurant;

import org.springframework.stereotype.Service;

@Service
public class RestaurantCategoryService {
    private final RestaurantCategoryRepository rcRepository;

    public RestaurantCategoryService(RestaurantCategoryRepository rcRepository) {
        this.rcRepository = rcRepository;
    }
}
