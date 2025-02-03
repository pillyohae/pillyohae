package com.example.pillyohae.product.repository;

import com.example.pillyohae.product.entity.Product;
import com.example.pillyohae.recommendation.dto.RecommendationKeywordDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductCustomRepository {

    Page<Product> getAllProduct(String productName, String companyName, String categoryName, Pageable pageable);

    List<Product> findProductsByNameLike(RecommendationKeywordDto[] recommendations);
}
