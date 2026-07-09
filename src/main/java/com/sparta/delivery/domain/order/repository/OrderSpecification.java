package com.sparta.delivery.domain.order.repository;


import com.sparta.delivery.domain.order.entity.Order;
import com.sparta.delivery.global.common.Enums;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

/**
 * 주문 목록 조회용 동적 검색조건 모음
 * 각 메서드는 조건값이 null 이면 null Specification을 반환
 * Specification.and()는 null을 안전하게 무시하므로 그대로 체이닝하면 됨
 */
public class OrderSpecification {

    private OrderSpecification() {
    }

    public static Specification<Order> customerIdEquals(Long customerId) {
        return (root, query, cb) -> cb.equal(root.get("customer").get("id"), customerId);
    }

    public static Specification<Order> orderStatusEquals(Enums.OrderStatus orderStatus) {
        if (orderStatus == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("orderStatus"), orderStatus);
    }

    public static Specification<Order> restaurantIdEquals(UUID restaurantId) {
        if (restaurantId == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("restaurant").get("id"), restaurantId);
    }

    // startDate ~ endDate (둘 중 하나만 있어도 동작, 날짜는 하루 전체 범위로 처리)
    public static Specification<Order> orderedAtBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null && endDate == null) {
            return null;
        }
        return (root, query, cb) -> {
            if (startDate != null && endDate != null) {
                LocalDateTime start = startDate.atStartOfDay();
                LocalDateTime end = endDate.atTime(23, 59, 59);
                return cb.between(root.get("orderedAt"), start, end);
            }
            if (startDate != null) {
                return cb.greaterThanOrEqualTo(root.get("orderedAt"), startDate.atStartOfDay());
            }
            return cb.lessThanOrEqualTo(root.get("orderedAt"), endDate.atTime(23, 59, 59));
        };
    }
}
