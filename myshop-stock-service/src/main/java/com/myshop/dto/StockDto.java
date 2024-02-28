package com.myshop.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StockDto {
    private Long orderId;
    private Long userId;
    private int stockQuantity;
}
