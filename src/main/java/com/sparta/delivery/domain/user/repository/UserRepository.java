package com.sparta.delivery.domain.user.repository;

import com.sparta.delivery.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    default User findByIdOrElseThrow(Long id) {
        return findById(id).orElseThrow(() -> new NullPointerException("등록된 사용자가 없습니다."));
    }
}
