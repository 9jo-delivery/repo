package com.sparta.delivery.domain.menu.service;

import com.sparta.delivery.domain.menu.dto.MenuOptionGroupCreateRequest;
import com.sparta.delivery.domain.menu.entity.Menu;
import com.sparta.delivery.domain.menu.entity.MenuOptionGroup;
import com.sparta.delivery.domain.menu.repository.MenuOptionGroupRepository;
import com.sparta.delivery.domain.menu.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuOptionGroupService {

    private final MenuOptionGroupRepository menuOptionGroupRepository;
    private final MenuRepository menuRepository;

    @Transactional
    public UUID createMenuOptionGroup(UUID menuId, MenuOptionGroupCreateRequest request) {

        // 최소 선택 개수가 최대 선택 개수보다 크면 안된다는 비즈니스 검증
        if(request.minSelect() > request.maxSelect()) {
            throw new IllegalArgumentException("최소 선택 개수는 최대 선택 개수보다 클 수 없습니다.");
        }

        // 메뉴 조회
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메뉴입니다."));

        // request에서 데이터를 가져와 메뉴 옵션 그룹과 결합해 빌드
        MenuOptionGroup menuOptionGroup = MenuOptionGroup.builder()
                .menu(menu)
                .name(request.name())
                .isRequired(request.isRequired())
                .minSelect(request.minSelect())
                .maxSelect(request.maxSelect())
                .sortOrder(request.sortOrder())
                .build();

        menuOptionGroupRepository.save(menuOptionGroup);
        return menuOptionGroup.getId();
    }
}
