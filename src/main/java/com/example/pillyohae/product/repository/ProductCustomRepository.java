package com.example.pillyohae.product.repository;

import com.example.pillyohae.product.entity.Product;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductCustomRepository {

    Page<Product> getAllProduct(String productName, String companyName, String categoryName,
        Pageable pageable);

    List<Product> findProductsByNameLike(List<String> recommendedProductNames);
}
