package com.example.categoryservice.service;

import com.example.categoryservice.domain.Product;
import com.example.categoryservice.dto.CartOrderInfoDto;
import com.example.categoryservice.dto.SingleOrderInfoDto;
import com.example.categoryservice.exception.LackStockException;
import com.example.categoryservice.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final RestTemplate restTemplate;
    @Transactional
    public void updateStockByOrder(String productCode, int qty){
        productRepository.updateStockByOrder(qty, productCode);
    }
    @Transactional
    public Optional<Product> findProductByCode(String productCode){
         return productRepository.findByProductCode(productCode);
    }
    @Transactional
    public void updateStock(String productCode, int qty, CartOrderInfoDto dto){
        Product product = findProductByCode(productCode).orElseThrow(
                ()-> new IllegalArgumentException("해당 재고가 없습니다.")
        );
        log.info("현재 남아있는 재고: ;{}", product.getStock());
        if (product.getStock()-qty <0){
            if (dto != null){
                sendExceptionAlarmForRollback(dto);
            }
            throw new LackStockException("남은 재고가 없습니다.");
        }
        updateStockByOrder(productCode, qty);
        product = findProductByCode(productCode).get();
        log.info("업데이트 후 남아있는 재고 : {}", product.getStock());
    }
    public void sendExceptionAlarmForRollback(CartOrderInfoDto dto){
        String url = "http://localhost:8083/cart/order/rollback";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CartOrderInfoDto> entity = new HttpEntity<>(dto, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        log.info("응답코드 : " + response.getStatusCode());
        log.info("응답본문 : " + response.getBody());
    }
    @Transactional
    public void updateSingleStock(SingleOrderInfoDto dto){
        updateStock(dto.productCode(), dto.qty(), null);
    }
    @Transactional
    public void updateAllStock(CartOrderInfoDto dto){
        Map<String, Integer> orderMap= dto.getOrderMap();
        for(String productCode : orderMap.keySet()){
            updateStock(productCode, orderMap.get(productCode), dto);
        }
    }
}
