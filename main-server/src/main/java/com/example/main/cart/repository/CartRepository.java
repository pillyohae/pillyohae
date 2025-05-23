package com.example.main.cart.repository;

import com.example.common.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long>, CartQueryRepository {

    // Cart를 가져올때 product정보까지 한번에 가져온다
    @Query("SELECT c FROM Cart c JOIN FETCH c.product WHERE c.user.id = :userId")
    List<Cart> findCartsWithProductsByUserId(@Param("userId") Long userId);

    @Modifying
    void deleteByUserId(Long userId);

    Cart findByProduct_ProductIdAndUserId(Long productId, Long userId);
}
