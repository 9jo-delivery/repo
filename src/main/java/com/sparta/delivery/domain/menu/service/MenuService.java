package com.sparta.delivery.domain.menu.service;

import com.sparta.delivery.domain.ai.service.aiToClient.AiTotalService;
import com.sparta.delivery.domain.menu.dto.MenuCreateRequest;
import com.sparta.delivery.domain.menu.dto.MenuCreateResponse;
import com.sparta.delivery.domain.menu.dto.MenuSearchResponse;
import com.sparta.delivery.domain.menu.dto.MenuUpdateRequest;
import com.sparta.delivery.domain.menu.entity.Menu;
import com.sparta.delivery.domain.menu.repository.MenuRepository;
import com.sparta.delivery.domain.restaurant.entity.Restaurant;
import com.sparta.delivery.domain.restaurant.repository.RestaurantRepository;
import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuService {

    private final MenuRepository menuRepository;
    private final RestaurantRepository restaurantRepository;
    private final AiTotalService aiTotalService;

    // 메뉴 등록
    @Transactional
    public MenuCreateResponse createMenu(UUID restaurantId, MenuCreateRequest request, Long userId) {

        // RestaurantRepository를 통해 가게 조회
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("가게를 찾을 수 없습니다."));

        // 본인 가게 검증
        if (!restaurant.getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("본인 가게의 메뉴만 등록할 수 있습니다.");
        }

        // request에서 데이터를 꺼내고, 가게 객체를 엮어서 Menu 엔티티를 빌드
        Menu menu = Menu.builder()
                .restaurant(restaurant)
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .build();

        //DB 저장 후 만들어진 메뉴 ID 반환
        Menu savedMenu = menuRepository.save(menu);

        // AI 생성 설명을 원했다면 (aiGenerateDescription=true일때)
        // NullPointerException 방어를 위해 Boolean.TRUE.equals() 적용
        if (Boolean.TRUE.equals(request.aiGenerateDescription())) {
            // 실제 사장님 객체 꺼내기
            User owner = restaurant.getOwner();

            // AI 설명 생성 요청 (저장된 savedMenu를 넘겨줍니다)
            String finalDescription = aiTotalService.generateMenuDescript(owner, restaurant, savedMenu);

            // 더티 체킹 (AI 설명으로 엔티티 업데이트)
            savedMenu.updateDescriptions(finalDescription);
        }

        return MenuCreateResponse.from(savedMenu);
    }

    // 메뉴 목록 검색 (가게 메뉴 전체 조회)
    public Page<MenuSearchResponse> getMenusByRestaurant(UUID restaurantId, Pageable pageable) {

        if(!restaurantRepository.existsById(restaurantId)){
            throw new ResourceNotFoundException("존재하지 않는 가게입니다.");
        }

        // TODO: 나중에 name 조건을 포함하여 조회하는 리포지토리 메서드로 확장해야 함
        Page<Menu> menus = menuRepository.findAllByRestaurantIdAndIsHiddenFalse(restaurantId, pageable);

        return menus.map(MenuSearchResponse::from);
    }

    // 메뉴 상세 조회 (단일 메뉴 조회)
    // 클래스 상단에 @Transactional(readOnly = true)가 이미 붙어있음
    public MenuSearchResponse getMenuDetails(UUID menuId) {

        // DB에서 UUID 기반으로 메뉴를 찾고, 없으면 예외 처리
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 메뉴입니다."));

        // static 정적 메서드(from)를 사용해 가방에 실제 값을 채워서 반환!
        return MenuSearchResponse.from(menu);
    }

    // 메뉴 수정
    @Transactional
    public MenuSearchResponse updateMenu(UUID menuId, MenuUpdateRequest request, Long userId) {
        // 존재하는 메뉴인지 검증
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 메뉴입니다."));

        // 본인 가게 검증
        if (!menu.getRestaurant().getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("본인 가게의 메뉴만 수정할 수 있습니다.");
        }

        String finalDescription;
        if (Boolean.TRUE.equals(request.aiGenerateDescription())) {
            User owner = menu.getRestaurant().getOwner();
            finalDescription = aiTotalService.generateMenuDescript(owner, menu.getRestaurant(), menu);
        } else {
            finalDescription = request.description();
        }

        menu.update(
                request.name(),
                finalDescription,
                request.price(),
                request.isHidden(),
                request.isSoldOut()
        );

        return MenuSearchResponse.from(menu);
    }

    // 메뉴 삭제
    @Transactional
    public void deleteMenu(UUID menuId, Long userId) {

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 메뉴입니다."));

        // 본인 가게 검증
        if(!menu.getRestaurant().getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("본인 가게의 메뉴만 삭제할 수 있습니다.");
        }

        menu.markAsDeleted(userId);
    }

}
