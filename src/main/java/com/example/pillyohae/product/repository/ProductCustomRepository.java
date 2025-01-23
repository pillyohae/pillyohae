package com.example.pillyohae.product.repository;

import com.example.pillyohae.product.entity.Product;
import com.example.pillyohae.recommendation.dto.RecommendationKeywordDto;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductCustomRepository {

    Page<Product> getAllProduct(String productName, String companyName, String category, Pageable pageable);

    List<Product> findProductsByNameLike(RecommendationKeywordDto[] recommendations);
}
