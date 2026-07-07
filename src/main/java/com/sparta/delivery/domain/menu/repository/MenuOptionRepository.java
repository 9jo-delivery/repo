package com.sparta.delivery.domain.menu.repository;

import com.sparta.delivery.domain.menu.entity.MenuOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MenuOptionRepository extends JpaRepository<MenuOption, UUID> {
}
