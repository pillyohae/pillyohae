package com.example.pillyohae.cart.repository;

import com.example.pillyohae.cart.dto.CartProductDetailResponseDto;
import com.example.pillyohae.cart.entity.Cart;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    @Query("SELECT " +
        "c.product.productId," +
        "c.product.productName," +
        "c.product.imageUrl," +
        "c.product.price," +
        "c.quantity " +
        "FROM Cart c " +
        "WHERE c.user.id = :userId")
    List<CartProductDetailResponseDto> findCartDtoListByUserId(Long userId);


    // Cart를 가져올때 product정보까지 한번에 가져온다
    @Query("SELECT c FROM Cart c JOIN FETCH c.product WHERE c.user.id = :userId")
    List<Cart> findCartsWithProductsByUserId(@Param("userId") Long userId);



}
