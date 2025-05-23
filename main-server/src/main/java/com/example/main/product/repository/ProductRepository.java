package com.example.main.product.repository;

import com.example.common.product.entity.Product;
import com.example.main.product.dto.product.ProductRecommendationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductCustomRepository {

    Page<Product> findProductsByUserId(Long userId, Pageable pageable);

    @Query("select p from Product p left join fetch p.images where p.productId in :productIds")
    List<Product> findByProductIdInJoinImage(Collection<Long> productIds);

    @Query(
        "SELECT new com.example.main.product.dto.product.ProductRecommendationDto(p.productName) "
            + "FROM Product p "
    )
    List<ProductRecommendationDto> findAllProductsName();
}


