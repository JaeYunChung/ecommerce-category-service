package com.example.categoryservice.kafka;

import com.example.categoryservice.dto.CartOrderInfoDto;

import com.example.categoryservice.dto.SingleOrderInfoDto;
import com.example.categoryservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class OrderInfoConsumer {
    private final ProductService productService;
    @Bean
    public Consumer<CartOrderInfoDto> receiveCartOrderInfo(){
        return productService::updateAllStock;
    }
    @Bean
    public Consumer<SingleOrderInfoDto> receiveSingleOrderInfo(){
        return productService::updateSingleStock;
    }
}
