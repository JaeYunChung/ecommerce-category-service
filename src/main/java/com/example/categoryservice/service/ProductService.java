package com.example.categoryservice.service;

import com.example.categoryservice.domain.Product;
import com.example.categoryservice.dto.OrderInfoDto;
import com.example.categoryservice.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    @Transactional
    public void updateStockByOrder(OrderInfoDto dto){
        productRepository.updateStockByOrder(dto.qty(), dto.productCode());
    }
    @Transactional
    public Optional<Product> findProductByCode(String productCode){
         return productRepository.findByProductCode(productCode);
    }
}
