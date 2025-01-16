package com.example.pillyohae.product.repository;

import com.example.pillyohae.product.entity.Product;
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


    List<Long> findPriceByProductIdIn(Collection<Long> productIds);
}


