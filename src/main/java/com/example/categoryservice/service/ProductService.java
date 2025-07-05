package com.example.categoryservice.service;

import com.example.categoryservice.domain.Product;
import com.example.categoryservice.dto.OrderInfoDto;
import com.example.categoryservice.dto.PaymentRequest;
import com.example.categoryservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final RestTemplate restTemplate;
    @Transactional
    public void updateStockByCode(String productCode, int qty){
        productRepository.updateStock(qty, productCode);
    }
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public Optional<Product> findProductByCode(String productCode){
         return productRepository.findByProductCode(productCode);
    }
    @Transactional
    public void updateStock(OrderInfoDto dto, List<OrderInfoDto> dtos){
        Product product = findProductByCode(dto.productCode()).orElseThrow(
                ()-> new IllegalArgumentException("해당 재고가 없습니다.")
        );
        boolean flag = true;
        log.info("현재 남아있는 재고: {}", product.getStock());
        if (product.getStock()-dto.qty() <0){
            sendExceptionAlarmForRollback(dtos);
            flag=false;
        }// 재고 서버에서 오류가 나면 롤백 진행
        if (flag) {
            updateStockByCode(dto.productCode(), dto.qty());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        product = findProductByCode(dto.productCode()).get();
        log.info("업데이트 후 남아있는 재고 : {}", product.getStock());
    }
    public void sendPaymentInfoToPayServer(long memberId, long totalPrice, List<OrderInfoDto> info){
        String url = "http://localhost:8084/pay/product";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        PaymentRequest dto = new PaymentRequest(memberId, totalPrice);
        HttpEntity<PaymentRequest> entity = new HttpEntity<>(dto, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        HttpStatusCode code = response.getStatusCode();
        // 만약 결제 서버에서 오류가 나면 장바구니 주문의 경우 추가 롤백을 진행해야함
        if (code.is4xxClientError()){
            log.info("오류가 발생했고 롤백을 수행합니다.");
            sendExceptionAlarmForRollback(info);
        }
        log.info(response.getBody().toString());
    }
    public void sendExceptionAlarmForRollback(List<OrderInfoDto> dto){
        String url = "http://localhost:8083/order/rollback";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<List<OrderInfoDto>> entity = new HttpEntity<>(dto, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        log.info("응답코드 : " + response.getStatusCode());
        log.info("응답본문 : " + response.getBody());
    }
    @Transactional
    public void updateAllStock(List<OrderInfoDto> dto){
        if (dto.isEmpty()){
            throw new IllegalArgumentException("주문할 상품이 존재하지 않습니다.");
        }long memberId = dto.getFirst().memberId();
        for (OrderInfoDto order : dto){
            // 단품 결제에 실패하면 해당 상품이 장바구니에 추가되도록 로직 구성
            // 장바구니 결제에 실패하면 해당 상품들이 장바구니에 추가되도록 로직 구성
            updateStock(order, dto);
        }// 전체가격 계산 및 결제서버에 전달
        Set<String> productCodeSet = dto.stream()
                .map(OrderInfoDto::productCode)
                .collect(Collectors.toSet());
        long totalPrice = productRepository.getTotalPriceByCode(productCodeSet);
        sendPaymentInfoToPayServer(memberId, totalPrice, dto);
    }
}
