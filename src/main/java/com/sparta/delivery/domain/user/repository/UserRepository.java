package com.sparta.delivery.domain.user.repository;

import com.sparta.delivery.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    default User findByIdOrElseThrow(Long id) {
        return findById(id).orElseThrow(() -> new NullPointerException("등록된 사용자가 없습니다."));
    }

    @Query("SELECT u FROM User u WHERE u.username LIKE %:keyword% OR str(u.role) LIKE %:keyword%")
    Page<User> searchByUsernameOrRole(@Param("keyword") String keyword, Pageable pageable);
}
