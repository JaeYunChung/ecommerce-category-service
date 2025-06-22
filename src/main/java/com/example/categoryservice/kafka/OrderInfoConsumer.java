package com.example.categoryservice.kafka;

import com.example.categoryservice.domain.Product;
import com.example.categoryservice.dto.OrderInfoDto;

import com.example.categoryservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class OrderInfoConsumer {
    private final ProductService productService;
    @Bean
    public Consumer<OrderInfoDto> receiveOrderInfo(){
        return dto -> {
            Optional<Product> product = productService.findProductByCode(dto.productCode());
            if (product.isEmpty()) throw new IllegalArgumentException("해당 재고가 없습니다.");
            log.info("현재 남아있는 재고 : {}", product.get().getStock());
            productService.updateStockByOrder(dto);
            product = productService.findProductByCode(dto.productCode());
            log.info("업데이트 후 남아있는 재고 : {}", product.get().getStock());
        };
    }
}
