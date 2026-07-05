package com.sparta.delivery.domain.menu.service;

import com.sparta.delivery.domain.menu.dto.MenuOptionCreateRequest;
import com.sparta.delivery.domain.menu.entity.MenuOption;
import com.sparta.delivery.domain.menu.entity.MenuOptionGroup;
import com.sparta.delivery.domain.menu.repository.MenuOptionGroupRepository;
import com.sparta.delivery.domain.menu.repository.MenuOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuOptionService {

    private final MenuOptionRepository menuOptionRepository;
    private final MenuOptionGroupRepository menuOptionGroupRepository;

    @Transactional
    public UUID createMenuOption(MenuOptionCreateRequest request) {

        // 메뉴 옵션 그룹 조회
        MenuOptionGroup menuOptionGroup = menuOptionGroupRepository.findById(request.optionGroupId())
                .orElseThrow(() -> new IllegalArgumentException("메뉴 옵션 그룹을 찾을 수 없습니다."));

        // request에서 데이터를 꺼내고, 메뉴 옵션 그룹을 엮어서 MenuOption 엔티티를 빌드
        MenuOption menuOption = MenuOption.builder()
                .menuOptionGroup(menuOptionGroup)
                .name(request.name())
                .extraPrice(request.extraPrice())
                .sortOrder(request.sortOrder())
                .build();

        //DB 저장 후 만들어진 메뉴 옵션 ID 반환
        MenuOption menuOptionSaved = menuOptionRepository.save(menuOption);
        return menuOptionSaved.getId();
    }
}
