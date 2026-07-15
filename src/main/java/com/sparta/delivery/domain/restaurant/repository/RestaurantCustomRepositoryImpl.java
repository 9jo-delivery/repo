package com.sparta.delivery.domain.restaurant.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.delivery.domain.restaurant.entity.Restaurant;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.sparta.delivery.domain.restaurant.entity.QRestaurant.restaurant;

@Repository
@AllArgsConstructor
public class RestaurantCustomRepositoryImpl implements RestaurantCustomRepository{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Restaurant> searchRestaurants(UUID categoryId, UUID regionId, Boolean isOpen, String name, Pageable pageable) {
        // 페이지 데이터 조회
        List<Restaurant> queriedList = jpaQueryFactory
                .selectFrom(restaurant)
                .where(
                        nameContains(name),
                        categoryIdEq(categoryId),
                        regionIdEq(regionId),
                        isOpenEq(isOpen))
                .orderBy(getOrderSpecifier(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 count
        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(restaurant.count())
                .from(restaurant)
                .where(
                        nameContains(name),
                        categoryIdEq(categoryId),
                        regionIdEq(regionId),
                        isOpenEq(isOpen)
                );

        return PageableExecutionUtils.getPage(queriedList, pageable, countQuery::fetchOne);
    }

    private BooleanExpression nameContains(String name) {
        return restaurant.name.contains(name);
    }

    private BooleanExpression categoryIdEq(UUID categoryId) {
        return categoryId == null ? null : restaurant.category.id.eq(categoryId);
    }

    private BooleanExpression regionIdEq(UUID regionId) {
        return regionId == null ? null : restaurant.region.id.eq(regionId);
    }

    private BooleanExpression isOpenEq(Boolean isOpen) {
        return isOpen == null ? null : restaurant.isOpen.eq(isOpen);
    }

    private OrderSpecifier<?> getOrderSpecifier(Pageable pageable) {
        for (Sort.Order order : pageable.getSort()) {
            String property = order.getProperty();

            return switch (property) {
                case "createdAt" -> order.isAscending() ? restaurant.createdAt.asc() : restaurant.createdAt.desc();
                case "averageRating" -> order.isAscending() ? restaurant.averageRating.asc() : restaurant.averageRating.desc();
                case "reviewCount" -> order.isAscending() ? restaurant.reviewCount.asc() : restaurant.reviewCount.desc();
                case "deliveryFee" -> order.isAscending() ? restaurant.deliveryFee.asc() : restaurant.deliveryFee.desc();
                case "minOrderAmount" -> order.isAscending() ? restaurant.minOrderAmount.asc() : restaurant.minOrderAmount.desc();
                default -> restaurant.createdAt.desc();
            };
        }
        return restaurant.createdAt.desc();
    }
}
