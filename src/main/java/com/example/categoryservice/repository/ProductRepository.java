package com.example.categoryservice.repository;

import com.example.categoryservice.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Modifying
    @Query("update Product p set p.stock=p.stock-:qty where p.productCode=:productCode")
    void updateStockByOrder(int qty, String productCode);

    Optional<Product> findByProductCode(String productCode);
}
