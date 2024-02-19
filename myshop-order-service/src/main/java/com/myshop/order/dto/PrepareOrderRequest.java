package com.myshop.order.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class PrepareOrderRequest {
    private Long userId;
    private List<CreateOrderItemDto> orderItemDtos;
}