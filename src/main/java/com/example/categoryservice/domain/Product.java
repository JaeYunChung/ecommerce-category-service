package com.example.categoryservice.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productName;
    @Column(unique = true)
    private String productCode;
    private Long stock;
}
