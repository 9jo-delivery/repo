package com.sparta.delivery.domain.menu.repository;

import com.sparta.delivery.domain.menu.entity.MenuOptionGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MenuOptionGroupRepository extends JpaRepository<MenuOptionGroup, UUID> {
    @EntityGraph(attributePaths = {"menu"})
    Page<MenuOptionGroup> findAllByMenuId(UUID menuId, Pageable pageable);
}
