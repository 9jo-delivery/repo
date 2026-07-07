package com.sparta.delivery.domain.order.service;

import com.sparta.delivery.domain.menu.entity.Menu;
import com.sparta.delivery.domain.menu.repository.MenuRepository;
import com.sparta.delivery.domain.menu.repository.RestaurantRepository;
import com.sparta.delivery.domain.order.dto.CreatedOrderRequestDto;
import com.sparta.delivery.domain.order.dto.OrderItemRequestDto;
import com.sparta.delivery.domain.order.dto.OrderResponseDto;
import com.sparta.delivery.domain.order.entity.Order;
import com.sparta.delivery.domain.order.entity.OrderItem;
import com.sparta.delivery.domain.order.repository.OrderRepository;
import com.sparta.delivery.domain.restaurant.entity.Restaurant;
import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuRepository menuRepository;

    @Transactional
    public OrderResponseDto createOrder(Long customerId, CreatedOrderRequestDto request) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(
                        () -> new IllegalArgumentException("존재하지 않는 사용자입니다.")
                );

        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(
                        () -> new IllegalArgumentException("존재하지 않는 가게입니다.")
                );

        Order order = Order.create(
                customer,
                restaurant,
                generateOrderNumber(),
                request.getDeliveryAddress(),
                request.getDeliveryDetailAddress(),
                request.getDeliveryZipCode()
        );

        for (OrderItemRequestDto itemRequest : request.getOrderItems()){
            Menu menu = menuRepository.findById(itemRequest.getMenuId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메뉴입니다."));

            OrderItem orderItem = OrderItem.create(menu, itemRequest.getQuantity());

            List<UUID> optionIds = itemRequest.getSelectedOptionIds();

        }
    }

    //OrderNumber 고유값 자동생성
    private String generateOrderNumber(){
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "ORD-" + timestamp + "-" + random;
    }
}
