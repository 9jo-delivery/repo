package com.sparta.delivery.domain.menu.service;

import com.sparta.delivery.domain.menu.dto.MenuOptionCreateRequest;
import com.sparta.delivery.domain.menu.dto.MenuOptionCreateResponse;
import com.sparta.delivery.domain.menu.dto.MenuOptionSearchResponse;
import com.sparta.delivery.domain.menu.dto.MenuOptionUpdateRequest;
import com.sparta.delivery.domain.menu.entity.MenuOption;
import com.sparta.delivery.domain.menu.entity.MenuOptionGroup;
import com.sparta.delivery.domain.menu.repository.MenuOptionGroupRepository;
import com.sparta.delivery.domain.menu.repository.MenuOptionRepository;
import com.sparta.delivery.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuOptionService {

    private final MenuOptionRepository menuOptionRepository;
    private final MenuOptionGroupRepository menuOptionGroupRepository;

    // 옵션 항목 등록
    @Transactional
    public MenuOptionCreateResponse createMenuOption(UUID groupId, MenuOptionCreateRequest request, Long userId) {

        // 메뉴 옵션 그룹 조회
        MenuOptionGroup menuOptionGroup = menuOptionGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 메뉴 옵션 그룹입니다."));

        if (!menuOptionGroup.getMenu().getRestaurant().getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("본인 가게의 메뉴 옵션만 등록할 수 있습니다.");
        }

        // request에서 데이터를 꺼내고, 메뉴 옵션 그룹을 엮어서 MenuOption 엔티티를 빌드
        MenuOption menuOption = MenuOption.builder()
                .menuOptionGroup(menuOptionGroup)
                .name(request.name())
                .extraPrice(request.extraPrice())
                .sortOrder(request.sortOrder())
                .build();

        //DB 저장 후 만들어진 메뉴 옵션 ID 반환
        MenuOption menuOptionSaved = menuOptionRepository.save(menuOption);
        return MenuOptionCreateResponse.from(menuOptionSaved);
    }

    // 옵션 항목 목록 검색
    public Page<MenuOptionSearchResponse> getOptionByGroup(UUID groupId, Pageable pageable) {

        if (!menuOptionGroupRepository.existsById(groupId)) {
            throw new ResourceNotFoundException("존재하지 않는 메뉴 옵션 그룹입니다.");
        }

        // TODO : 모든 Service 중복 코드 글로벌 유틸 클래스로 빼기
        Sort sort = pageable.getSort().isSorted() ? pageable.getSort() : Sort.by(Sort.Direction.DESC, "createdAt");
        int size = pageable.getPageSize();
        if (size != 10 && size != 30 && size != 50) {
            size = 10;
        }

        pageable = PageRequest.of(pageable.getPageNumber(), size, sort);
        Page<MenuOption> menuOptions = menuOptionRepository.findAllByMenuOptionGroupId(groupId, pageable);
        return menuOptions.map(MenuOptionSearchResponse::from);
    }

    // 옵션 항목 상세 조회
    public MenuOptionSearchResponse getOptionById(UUID optionId) {

        MenuOption option = menuOptionRepository.findById(optionId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 메뉴 옵션입니다."));

        return MenuOptionSearchResponse.from(option);
    }

    // 옵션 항목 수정
    @Transactional
    public MenuOptionSearchResponse updateOption(UUID optionId, MenuOptionUpdateRequest request, Long userId) {

        MenuOption option = menuOptionRepository.findById(optionId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 메뉴 옵션입니다."));

        if (!option.getMenuOptionGroup().getMenu().getRestaurant().getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("본인 가게의 메뉴 옵션만 수정할 수 있습니다.");
        }

        option.update(
                request.name(),
                request.extraPrice(),
                request.isSoldOut(),
                request.sortOrder()
        );
        return MenuOptionSearchResponse.from(option);
    }

    // 옵션 항목 삭제
    @Transactional
    public void deleteOption(UUID optionId, Long userId) {

        MenuOption option = menuOptionRepository.findById(optionId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 메뉴 옵션입니다."));

        if (!option.getMenuOptionGroup().getMenu().getRestaurant().getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("본인 가게의 메뉴 옵션만 삭제할 수 있습니다.");
        }

        option.markAsDeleted(userId);
    }

}
