package com.example.pillyohae.product.repository;

import com.example.pillyohae.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByProductNameAndCompanyNameAndCategory(String productName, String companyName, String category);

    List<Product> findByProductNameAndCompanyName(String productName, String companyName);

    List<Product> findByProductNameAndCategory(String productName, String category);

    List<Product> findByCompanyNameAndCategory(String companyName, String category);

    List<Product> findByProductName(String productName);

    List<Product> findByCompanyName(String companyName);

    List<Product> findByCategory(String category);

    List<Product> findProductsByUserId(Long userId);

}


