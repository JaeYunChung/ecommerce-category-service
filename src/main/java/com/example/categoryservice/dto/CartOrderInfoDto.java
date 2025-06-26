package com.example.categoryservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class CartOrderInfoDto {
    private final Map<String, Integer> orderMap;
    private Long memberId;
    public CartOrderInfoDto(Long memberId, Map<String, Integer> orderMap){
        this.orderMap=orderMap;
        this.memberId=memberId;
    }
}