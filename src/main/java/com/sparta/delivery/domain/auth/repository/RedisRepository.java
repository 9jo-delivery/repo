package com.sparta.delivery.domain.auth.repository;

import com.sparta.delivery.domain.auth.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RedisRepository extends CrudRepository<RefreshToken, String> {
}
