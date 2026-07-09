package com.sparta.delivery.domain.menu.service;

import com.sparta.delivery.domain.menu.dto.MenuCreateRequest;
import com.sparta.delivery.domain.menu.dto.MenuResponse;
import com.sparta.delivery.domain.menu.dto.MenuUpdateRequest;
import com.sparta.delivery.domain.menu.entity.Menu;
import com.sparta.delivery.domain.menu.repository.MenuRepository;
import com.sparta.delivery.domain.restaurant.entity.Restaurant;
import com.sparta.delivery.domain.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuService {

    private final MenuRepository menuRepository;
    private final RestaurantRepository restaurantRepository; // 추후 import 수정할 것

    // 메뉴 등록
    @Transactional
    public UUID createMenu(UUID restaurantId, MenuCreateRequest request) {

        // RestaurantRepository를 통해 가게 조회
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다."));

        // aiGenerateDescription=true일때
        String finalDescription = request.description();
//        if (request.aiGenerateDescription != null && request.aiGenerateDescription) {
//            finalDescription = geminiService.generate(request.aiPrompt());
//        }

        // request에서 데이터를 꺼내고, 가게 객체를 엮어서 Menu 엔티티를 빌드
        Menu menu = Menu.builder()
                .restaurant(restaurant)
                .name(request.name())
                .description(finalDescription)
                .price(request.price())
                .isHidden(false)
                .isSoldOut(false)
                .build();

        //DB 저장 후 만들어진 메뉴 ID 반환
        Menu savedMenu = menuRepository.save(menu);
        return savedMenu.getId();
    }

    // 메뉴 목록 검색 (가게 메뉴 전체 조회)
    public Page<MenuResponse> getMenusByRestaurant(UUID restaurantId, Pageable pageable) {

        if(!restaurantRepository.existsById(restaurantId)){
            throw new IllegalArgumentException("존재하지 않는 가게입니다.");
        }

        Page<Menu> menus = menuRepository.findAllByRestaurantIdAndIsHiddenFalse(restaurantId, pageable);

        return menus.map(MenuResponse::from);
    }

    // 메뉴 상세 조회 (단일 메뉴 조회)
    // 클래스 상단에 @Transactional(readOnly = true)가 이미 붙어있음
    public MenuResponse getMenuDetails(UUID menuId) {

        // DB에서 UUID 기반으로 메뉴를 찾고, 없으면 예외 처리
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메뉴입니다."));

        // static 정적 메서드(from)를 사용해 가방에 실제 값을 채워서 반환!
        return MenuResponse.from(menu);
    }

    // 메뉴 수정
    @Transactional
    public MenuResponse updateMenu(UUID menuId, MenuUpdateRequest request) {
        // 존재하는 메뉴인지 검증
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메뉴입니다."));

        String finalDescription = request.description();
//        if (request.aiGenerateDescription != null && request.aiGenerateDescription) {
//            finalDescription = geminiService.generate(request.aiPrompt());
//        }

        menu.update(
                request.name(),
                finalDescription,
                request.price(),
                request.isHidden(),
                request.isSoldOut()
        );

        return MenuResponse.from(menu);
    }

    // 메뉴 삭제
    @Transactional
    public void deleteMenu(UUID menuId, Long userId) {

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메뉴입니다."));

        menu.markAsDeleted(userId);
    }

}
