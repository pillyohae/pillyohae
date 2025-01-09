package com.example.pillyohae.product.repository;

import com.example.pillyohae.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductCustomRepository {

    Page<Product> getAllProduct(String productName, String companyName, String category, Pageable pageable);
}
