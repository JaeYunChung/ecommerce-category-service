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
            productService.updateStockByOrder(dto);
            Optional<Product> product = productService.findProductByCode(dto.productCode());
            if (product.isEmpty()) throw new IllegalArgumentException("해당 재고가 없습니다.");
            Product p = product.get();
            log.info("{}의 남은 재고가 {} 남았습니다.", p.getProductName(), p.getStock());
        };
    }
}
