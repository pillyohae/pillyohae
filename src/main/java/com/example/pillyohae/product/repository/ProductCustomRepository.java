package com.example.pillyohae.product.repository;

import com.example.pillyohae.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductCustomRepository {

    Page<Product> getAllProduct(String productName, String companyName, String category, Pageable pageable);
}
