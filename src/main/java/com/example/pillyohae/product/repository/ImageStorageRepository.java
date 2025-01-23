package com.example.pillyohae.product.repository;

import com.example.pillyohae.product.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ImageStorageRepository extends JpaRepository<ProductImage, Long> {

    int countByProduct_ProductId(Long productProductId);

    List<ProductImage> findByProduct_ProductId(Long productId); //TODO leftjoin 추가 ?? fetchjoin

    @Query("SELECT MAX(pi.position) FROM ProductImage pi WHERE pi.product.productId = :productId")
    Optional<Integer> findMaxPositionByProductId(@Param("productId") Long productId);

    @Transactional
    @Modifying
    @Query("UPDATE ProductImage pi SET pi.position = pi.position - 1 WHERE pi.product.productId = :productId AND pi.position > :deletedPosition")
    void updatePositionsAfterDelete(@Param("productId") Long productId, @Param("deletedPosition") Integer deletedPosition);

    @Modifying
    @Transactional
    @Query("UPDATE ProductImage p SET p.position = p.position + 1 WHERE p.product.productId = :productId")
    void incrementAllPositions(@Param("productId") Long productId);

    ProductImage findByProduct_ProductIdAndPosition(Long productId, int position);
}
