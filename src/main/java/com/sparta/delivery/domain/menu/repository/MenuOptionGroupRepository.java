package com.sparta.delivery.domain.menu.repository;

import com.sparta.delivery.domain.menu.entity.MenuOptionGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MenuOptionGroupRepository extends JpaRepository<MenuOptionGroup, UUID> {
}
