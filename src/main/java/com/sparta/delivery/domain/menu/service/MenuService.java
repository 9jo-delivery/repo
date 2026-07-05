package com.sparta.delivery.domain.menu.service;

import com.sparta.delivery.domain.menu.dto.MenuCreateRequest;
import com.sparta.delivery.domain.menu.dto.MenuResponse;
import com.sparta.delivery.domain.menu.entity.Menu;
import com.sparta.delivery.domain.menu.repository.MenuRepository;
import com.sparta.delivery.domain.menu.repository.RestaurantRepository; // menu 패키지에 만든 껍대기 repository
import com.sparta.delivery.domain.restaurant.Restaurant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuService {

    private final MenuRepository menuRepository;
    private final RestaurantRepository restaurantRepository; // 추후 import 수정할 것

    // 메뉴 생성
    @Transactional
    public UUID createMenu(MenuCreateRequest request) {

        // RestaurantRepository를 통해 가게 조회
        Restaurant restaurant = restaurantRepository.findById(request.restaurantId())
                .orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다."));

        // request에서 데이터를 꺼내고, 가게 객체를 엮어서 Menu 엔티티를 빌드
        Menu menu = Menu.builder()
                .restaurant(restaurant)
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .build();

        //DB 저장 후 만들어진 메뉴 ID 반환
        Menu savedMenu = menuRepository.save(menu);
        return savedMenu.getId();
    }

    // 단일 메뉴 조회
    // 클래스 상단에 @Transactional(readOnly = true)가 이미 붙어있음
    public MenuResponse getMenuDetails(UUID menuId) {

        // DB에서 UUID 기반으로 메뉴를 찾고, 없으면 예외 처리
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메뉴입니다."));

        // static 정적 메서드(from)를 사용해 가방에 실제 값을 채워서 반환!
        return MenuResponse.from(menu);
    }

}
