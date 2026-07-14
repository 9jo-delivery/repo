package com.sparta.delivery.domain.menu.service;

import com.sparta.delivery.domain.menu.dto.MenuOptionGroupCreateRequest;
import com.sparta.delivery.domain.menu.dto.MenuOptionGroupCreateResponse;
import com.sparta.delivery.domain.menu.dto.MenuOptionGroupSearchResponse;
import com.sparta.delivery.domain.menu.dto.MenuOptionGroupUpdateRequest;
import com.sparta.delivery.domain.menu.entity.Menu;
import com.sparta.delivery.domain.menu.entity.MenuOptionGroup;
import com.sparta.delivery.domain.menu.repository.MenuOptionGroupRepository;
import com.sparta.delivery.domain.menu.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuOptionGroupService {

    private final MenuOptionGroupRepository menuOptionGroupRepository;
    private final MenuRepository menuRepository;

    // 옵션 그룹 등록
    @Transactional
    public MenuOptionGroupCreateResponse createMenuOptionGroup(UUID menuId, MenuOptionGroupCreateRequest request, Long userId) {

        // 메뉴 조회
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메뉴입니다."));

        // 본인 가게 메뉴 검증
        if (!menu.getRestaurant().getOwner().getId().equals(userId)) {
            throw new IllegalArgumentException("본인 가게의 옵션 그룹 메뉴만 등록할 수 있습니다.");
        }

        // 최소 선택 개수가 최대 선택 개수보다 크면 안된다는 비즈니스 검증
        if(request.minSelect() > request.maxSelect()) {
            throw new IllegalArgumentException("최소 선택 개수는 최대 선택 개수보다 클 수 없습니다.");
        }

        // request에서 데이터를 가져와 메뉴 옵션 그룹과 결합해 빌드
        MenuOptionGroup menuOptionGroup = MenuOptionGroup.builder()
                .menu(menu)
                .name(request.name())
                .isRequired(request.isRequired())
                .minSelect(request.minSelect())
                .maxSelect(request.maxSelect())
                .sortOrder(request.sortOrder())
                .build();

        MenuOptionGroup optionGroup = menuOptionGroupRepository.save(menuOptionGroup);
        return MenuOptionGroupCreateResponse.from(optionGroup);
    }

    // 옵션 그룹 목록 검색
    public Page<MenuOptionGroupSearchResponse> getOptionGroupByMenu (UUID menuId, Pageable pageable) {

        if(!menuRepository.existsById(menuId)){
            throw new IllegalArgumentException("존재하지 않는 메뉴입니다.");
        }

        // 기본 정렬값이 없을 경우 생성일자 기준 내림차순 정렬
        Sort sort = pageable.getSort().isSorted() ? pageable.getSort() : Sort.by(Sort.Direction.DESC, "createdAt");

        int size = pageable.getPageSize();
        if (size != 10 && size != 30 && size != 50) {
            size = 10;
        }

        pageable = PageRequest.of(pageable.getPageNumber(), size, sort);

        Page<MenuOptionGroup> optionGroups = menuOptionGroupRepository.findAllByMenuId(menuId, pageable);
        return optionGroups.map(MenuOptionGroupSearchResponse::from);
    }

    // 옵션 그룹 상세 조회
    public MenuOptionGroupSearchResponse getOptionGroupDetails(UUID groupId) {

        // DB에서 UUID 기반으로 옵션 그룹을 찾고, 없으면 예외 처리
        MenuOptionGroup optionGroup = menuOptionGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 옵션 그룹입니다."));

        return MenuOptionGroupSearchResponse.from(optionGroup);
    }

    // 옵션 그룹 수정
    @Transactional
    public MenuOptionGroupSearchResponse updateOptionGroup(UUID groupId, MenuOptionGroupUpdateRequest request, Long userId) {

        // 존재하는 옵션 그룹인지 검증
        MenuOptionGroup optionGroup = menuOptionGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 옵션 그룹입니다."));

        // 본인 가게 검증
        if (!optionGroup.getMenu().getRestaurant().getOwner().getId().equals(userId)) {
            throw new IllegalArgumentException("본인 가게의 옵션 그룹만 수정할 수 있습니다.");
        }

        String newName = (request.name() != null && !request.name().isEmpty())
                ? request.name()
                : optionGroup.getName();
        Boolean newRequired = (request.isRequired() != null)
                ? request.isRequired()
                : optionGroup.isRequired();
        Integer newMinSelect = (request.minSelect() != null)
                ? request.minSelect()
                : optionGroup.getMinSelect();
        Integer newMaxSelect = (request.maxSelect() != null)
                ? request.maxSelect()
                : optionGroup.getMaxSelect();
        Integer newSortOrder = (request.sortOrder() != null)
                ? request.sortOrder()
                : optionGroup.getSortOrder();

        // 최소 선택 개수가 최대 선택 개수보다 크면 안된다는 비즈니스 검증
        if (newMinSelect > newMaxSelect) {
            throw new IllegalArgumentException("최소 선택 개수는 최대 선택 개수보다 클 수 없습니다.");
        }

        optionGroup.update(newName, newRequired, newMinSelect, newMaxSelect, newSortOrder);
        return MenuOptionGroupSearchResponse.from(optionGroup);
    }

    // 옵션 그룹 삭제
    @Transactional
    public void optionGroupDelete(UUID groupId, Long userId) {

        MenuOptionGroup optionGroup = menuOptionGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 옵션 그룹입니다"));

        if (!optionGroup.getMenu().getRestaurant().getOwner().getId().equals(userId)) {
            throw new IllegalArgumentException("본인 가게의 옵션 그룹만 삭제할 수 있습니다.");
        }

        optionGroup.markAsDeleted(userId);
    }
}
