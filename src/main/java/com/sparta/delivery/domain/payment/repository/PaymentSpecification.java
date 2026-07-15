package com.sparta.delivery.domain.payment.repository;

import com.sparta.delivery.domain.payment.entity.Payment;
import com.sparta.delivery.global.common.Enums;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

/**
 * 결제 목록 조회용 동적 검색조건 모음.
 * customerId는 CUSTOMER 역할이 본인 결제만 조회할 때 사용, MASTER는 null로 넘겨 전체 조회.
 */
public class PaymentSpecification {

    private PaymentSpecification() {
    }

    public static Specification<Payment> customerIdEquals(Long customerId) {
        if (customerId == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("customer").get("id"), customerId);
    }

    public static Specification<Payment> paymentStatusEquals(Enums.PaymentStatus paymentStatus) {
        if (paymentStatus == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("paymentStatus"), paymentStatus);
    }

    public static Specification<Payment> orderIdEquals(UUID orderId) {
        if (orderId == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("order").get("id"), orderId);
    }

    // 결제 생성일(createdAt) 기준 기간 검색
    public static Specification<Payment> createdAtBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null && endDate == null) {
            return null;
        }
        return (root, query, cb) -> {
            if (startDate != null && endDate != null) {
                LocalDateTime start = startDate.atStartOfDay();
                LocalDateTime end = endDate.atTime(23, 59, 59);
                return cb.between(root.get("createdAt"), start, end);
            }
            if (startDate != null) {
                return cb.greaterThanOrEqualTo(root.get("createdAt"), startDate.atStartOfDay());
            }
            return cb.lessThanOrEqualTo(root.get("createdAt"), endDate.atTime(23, 59, 59));
        };
    }
}