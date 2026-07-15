package com.sparta.delivery.domain.restaurant.repository;

import com.sparta.delivery.domain.restaurant.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.LockModeType;

public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {

    // for update 쿼리 날라감(데이터베이스 행 수준의 배타적 잠금)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r from  Restaurant r where r.id = :id")
    Optional<Restaurant> findByIdWithPessimisticLock(@Param("id") UUID id);}


