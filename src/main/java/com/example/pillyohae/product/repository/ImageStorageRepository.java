package com.example.pillyohae.product.repository;

import com.example.pillyohae.product.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ImageStorageRepository extends JpaRepository<ProductImage, Long> {

    @Query("SELECT MAX(i.position) FROM ProductImage i WHERE i.product.productId = :productId")
    Optional<Integer> findMaxPositionByProductId(@Param("productId") Long productId);

}
