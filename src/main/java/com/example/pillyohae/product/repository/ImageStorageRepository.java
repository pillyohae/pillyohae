package com.example.pillyohae.product.repository;

import com.example.pillyohae.product.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageStorageRepository extends JpaRepository<ProductImage, Long> {
}
