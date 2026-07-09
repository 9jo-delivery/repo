package com.sparta.delivery.domain.order.service;

import com.sparta.delivery.domain.menu.entity.Menu;
import com.sparta.delivery.domain.menu.entity.MenuOption;
import com.sparta.delivery.domain.menu.repository.MenuOptionRepository;
import com.sparta.delivery.domain.menu.repository.MenuRepository;
import com.sparta.delivery.domain.menu.repository.RestaurantRepository;
import com.sparta.delivery.domain.order.dto.CancelOrderRequestDto;
import com.sparta.delivery.domain.order.dto.CreatedOrderRequestDto;
import com.sparta.delivery.domain.order.dto.OrderDetailResponseDto;
import com.sparta.delivery.domain.order.dto.OrderItemRequestDto;
import com.sparta.delivery.domain.order.dto.OrderResponseDto;
import com.sparta.delivery.domain.order.dto.OrderSearchDto;
import com.sparta.delivery.domain.order.dto.OrderSummaryResponseDto;
import com.sparta.delivery.domain.order.dto.UpdateOrderStatusRequestDto;
import com.sparta.delivery.domain.order.entity.Order;
import com.sparta.delivery.domain.order.entity.OrderItem;
import com.sparta.delivery.domain.order.entity.OrderItemOption;
import com.sparta.delivery.domain.order.repository.OrderRepository;
import com.sparta.delivery.domain.order.repository.OrderSpecification;
import com.sparta.delivery.domain.restaurant.entity.Restaurant;
import com.sparta.delivery.domain.user.entity.User;
import com.sparta.delivery.domain.user.repository.UserRepository;
import com.sparta.delivery.global.common.Enums.OrderStatus;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    private final MenuOptionRepository menuOptionRepository;

    private static final long CANCELLABLE_MIN = 5L;

    //주문 생성
    @Override
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

    //주문 상세 조회
    @Override
    public OrderDetailResponseDto getOrderDetail(Long customerId, UUID orderId) {
        Order order = orderRepository.findByIdAndCustomer_Id(orderId, customerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));
        return OrderDetailResponseDto.from(order);
    }

    //주문 상태 변경
    @Override
    @Transactional
    public OrderResponseDto updateOrderStatus(UUID orderId, UpdateOrderStatusRequestDto request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

        applyStatus(order, request.getOrderStatus());

        return OrderResponseDto.from(order);
    }

    //주문 취소
    @Override
    @Transactional
    public OrderResponseDto cancelOrder(Long customerId, UUID orderId, CancelOrderRequestDto request) {
        Order order = orderRepository.findByIdAndCustomer_Id(orderId, customerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

        LocalDateTime deadline = order.getCreatedAt().plusMinutes(CANCELLABLE_MIN);
        if (LocalDateTime.now().isAfter(deadline)){
            throw new IllegalStateException("주문 생성 후 5분이 지나 취소할 수 없습니다.");
        }

        order.cancelled(request.getCancelReason());

        return OrderResponseDto.from(order);
    }

    @Override
    @Transactional
    public void deleteOrder(Long customerId, UUID orderId) {
        Order order = orderRepository.findByIdAndCustomer_Id(orderId, customerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

        if (order.getOrderStatus() != OrderStatus.COMPLETED && order.getOrderStatus() != OrderStatus.CANCELLED){
            throw new IllegalStateException("완료되었거나 취소된 주문만 삭제할 수 있습니다.");
        }

        order.markAsDeleted(customerId);
    }

    private void applyStatus(Order order, OrderStatus status){
        switch (status){
            case ACCEPTED -> order.accepted();
            case COOKED -> order.cooked();
            case DELIVERED -> order.delivered();
            case COMPLETED -> order.completed();
            case CANCELLED -> order.cancelled(null);
            default -> throw new IllegalArgumentException("허용되지 않는 주문 상태입니다: " + status);
        }
    }

}
