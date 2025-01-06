package com.example.pillyohae.product.repository;

import com.example.pillyohae.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
