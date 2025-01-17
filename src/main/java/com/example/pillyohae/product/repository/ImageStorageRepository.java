package com.example.pillyohae.product.repository;

import com.example.pillyohae.product.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ImageStorageRepository extends JpaRepository<ProductImage, Long> {

    @Query("SELECT MAX(pi.position) FROM ProductImage pi WHERE pi.product.productId = :productId")
    Optional<Integer> findMaxPositionByProductId(@Param("productId") Long productId);

    List<ProductImage> findByProduct_ProductId(Long productId);

    int countByProduct_ProductId(Long productProductId);

}
