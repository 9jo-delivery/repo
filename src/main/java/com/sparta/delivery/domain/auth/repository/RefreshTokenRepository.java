package com.sparta.delivery.domain.auth.repository;

import com.sparta.delivery.domain.auth.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
}
