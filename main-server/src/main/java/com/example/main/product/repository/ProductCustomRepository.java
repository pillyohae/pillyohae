package com.example.main.product.repository;

import com.example.common.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductCustomRepository {

    Page<Product> getAllProduct(String productName, String companyName, String categoryName,
                                Pageable pageable);

    List<Product> findProductsByNameLike(List<String> recommendedProductNames);
}
