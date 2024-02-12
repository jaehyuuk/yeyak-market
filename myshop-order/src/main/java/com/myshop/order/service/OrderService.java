package com.myshop.order.service;

import com.myshop.global.exception.BadRequestException;
import com.myshop.item.domain.Item;
import com.myshop.item.repository.ItemRepository;
import com.myshop.order.domain.Order;
import com.myshop.order.domain.OrderItem;
import com.myshop.order.domain.OrderStatus;
import com.myshop.order.dto.CreateOrderItemDto;
import com.myshop.order.dto.OrderDto;
import com.myshop.order.repository.OrderRepository;
import com.myshop.user.domain.User;
import com.myshop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long prepareOrder(Long userId, List<CreateOrderItemDto> orderItemDtos) {
        User user = findByUserId(userId);
        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.PREPARATION) // 주문 상태를 준비 상태로 설정
                .build();

        for (CreateOrderItemDto dto : orderItemDtos) {
            Item item = findByItemId(dto.getItemId());
            OrderItem orderItem = dto.toEntity(item);
            order.addOrderItem(orderItem);
        }
        orderRepository.save(order);
        return order.getId();
    }

    @Transactional
    public void completeOrder(Long orderId) {
        Order order = findByOrderId(orderId);
        order.processPayment();
        orderRepository.saveAndFlush(order);
        if (order.getStatus() == OrderStatus.FAIL) {
            throw new BadRequestException("결제 요청 실패");
        }
    }

    @Transactional(readOnly = true)
    public List<OrderDto> getUserOrders(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(OrderDto::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = findByOrderId(orderId);
        order.cancel();
    }

    @Transactional
    public void removeOrderItem(Long orderId, Long orderItemId) {
        Order order = findByOrderId(orderId);
        OrderItem orderItemToRemove = order.getOrderItems().stream()
                .filter(orderItem -> orderItem.getId().equals(orderItemId))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("주문 목록이 존재하지 않습니다."));

        order.removeOrderItem(orderItemToRemove);
    }

    @Transactional(readOnly = true)
    public List<OrderDto> getOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(OrderDto::of).collect(Collectors.toList());
    }

    @Transactional
    public void deleteOrder(Long orderId) {
        Order order = findByOrderId(orderId);
        orderRepository.delete(order);
    }

    private User findByUserId(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException("유저 정보를 찾을 수 없습니다.")
        );
    }

    private Order findByOrderId(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(
                () -> new BadRequestException("주문 내역이 존재하지 않습니다.")
        );
    }

    private Item findByItemId(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(
                () -> new BadRequestException("상품이 존재하지 않습니다.")
        );
    }

}