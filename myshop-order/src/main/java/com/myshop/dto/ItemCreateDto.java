package com.myshop.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ItemCreateDto {
    private String name;
    private String content;
    private int price;
    private int stockQuantity;
    private String type; // "REGULAR" 또는 "RESERVED"
    private LocalDateTime reservationStart;
    private LocalDateTime reservationEnd;
}
