package com.example.categoryservice.kafka;

import com.example.categoryservice.dto.OrderInfoDto;
import com.example.categoryservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class OrderInfoConsumer {
    private final ProductService productService;
    @Bean
    public Consumer<List<OrderInfoDto>> receiveOrderInfo(){
        return productService::updateAllStock;
    }
}
