package com.sparta.delivery.domain.order.service;

import com.sparta.delivery.domain.menu.entity.Menu;
import com.sparta.delivery.domain.menu.entity.MenuOption;
import com.sparta.delivery.domain.menu.repository.MenuOptionRepository;
import com.sparta.delivery.domain.menu.repository.MenuRepository;
import com.sparta.delivery.domain.order.dto.*;
import com.sparta.delivery.domain.order.entity.Order;
import com.sparta.delivery.domain.order.entity.OrderItem;
import com.sparta.delivery.domain.order.entity.OrderItemOption;
import com.sparta.delivery.domain.order.repository.OrderRepository;
import com.sparta.delivery.domain.order.repository.OrderSpecification;
import com.sparta.delivery.domain.restaurant.entity.Restaurant;
import com.sparta.delivery.domain.restaurant.repository.RestaurantRepository;
import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuRepository menuRepository;
    private final MenuOptionRepository menuOptionRepository;


    //주문 생성
    @Transactional
    public OrderResponseDto createOrder(Long customerId, CreatedOrderRequestDto request) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 가게입니다."));

        Order order = Order.create(
                customer,
                restaurant,
                generateOrderNumber(),
                request.getDeliveryAddress(),
                request.getDeliveryDetailAddress(),
                request.getDeliveryZipCode()
        );

        Map<UUID, Menu> menuMap = menuMap(request.getOrderItems());
        Map<UUID, MenuOption> menuOptionMap = optionMap(request.getOrderItems());

        for (OrderItemRequestDto itemRequest : request.getOrderItems()){
            OrderItem orderItem = createOrderItem(itemRequest, menuMap, menuOptionMap);
            order.addItem(orderItem);
        }

        Order saveOrder = orderRepository.save(order);
        return OrderResponseDto.from(saveOrder);
    }

    //menuId만 조회해오기
    private Map<UUID, Menu> menuMap(List<OrderItemRequestDto> itemRequest){
        Set<UUID> menuIds = itemRequest.stream()
                .map(OrderItemRequestDto::getMenuId)
                .collect(Collectors.toSet());

        return menuRepository.findAllById(menuIds).stream()
                .collect(Collectors.toMap(Menu::getId, Function.identity()));
    }

    //optionId만 조회해오기
    private Map<UUID, MenuOption> optionMap(List<OrderItemRequestDto> itemRequest){
        Set<UUID> optionIds = itemRequest.stream()
                .flatMap(item -> item.getSelectedOptionIds() == null
                        ? Stream.empty()
                        : item.getSelectedOptionIds().stream())
                .collect(Collectors.toSet());

        if (optionIds.isEmpty()){
            return Collections.emptyMap();
        }

        return menuOptionRepository.findAllById(optionIds).stream()
                .collect(Collectors.toMap(MenuOption::getId, Function.identity()));
    }

    //menuId, optionId 에서 id에 맞춰서 조합
    private OrderItem createOrderItem(
            OrderItemRequestDto itemRequest,
            Map<UUID, Menu> menuMap,
            Map<UUID, MenuOption> menuOptionMap
    ) {
        Menu menu = menuMap.get(itemRequest.getMenuId());
        if (menu == null){
            throw new IllegalArgumentException("존재하지 않는 메뉴입니다.");
        }

        OrderItem orderItem = OrderItem.create(menu, itemRequest.getQuantity());

        List<UUID> optionIds = itemRequest.getSelectedOptionIds();
        if (optionIds != null){
            for (UUID optionId : optionIds){
                MenuOption menuOption = menuOptionMap.get(optionId);
                if (menuOption == null){
                    throw new IllegalArgumentException("존재하지 않는 메뉴 옵션입니다.");
                }

                OrderItemOption orderItemOption = OrderItemOption.create(
                        menuOption.getId(),
                        menuOption.getMenuOptionGroup().getName(),
                        menuOption.getName(),
                        menuOption.getExtraPrice()
                );

                orderItem.addOption(orderItemOption);
            }
        }

        return orderItem;
    }

    //OrderNumber 고유값 자동생성
    private String generateOrderNumber(){
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "ORD-" + timestamp + "-" + random;
    }

    //주문 목록 검색
    @Override
    public Page<OrderSummaryResponseDto> getOrders(Long customerId, OrderSearchDto search, Pageable pageable) {
        Specification<Order> spec = Specification.allOf(
                OrderSpecification.customerIdEquals(customerId),
                OrderSpecification.orderStatusEquals(search.getOrderStatus()),
                OrderSpecification.restaurantIdEquals(search.getRestaurantId()),
                OrderSpecification.orderedAtBetween(
                        search.getStartDate(),
                        search.getEndDate()
                )
        );

        return orderRepository.findAll(spec, pageable).map(OrderSummaryResponseDto :: from);
    }


}
