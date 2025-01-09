package com.example.pillyohae.cart.repository;

import com.example.pillyohae.cart.dto.CartProductDetailResponseDto;
import com.example.pillyohae.cart.entity.Cart;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    /**
     * Retrieves a list of cart product details for a specific user.
     *
     * @param userId the unique identifier of the user whose cart products are to be fetched
     * @return a list of {@link CartProductDetailResponseDto} containing cart product information
     *         including product ID, name, image URL, price, and quantity
     * 
     * @throws IllegalArgumentException if the provided userId is null
     */
    @Query("SELECT new com.example.pillyohae.cart.dto.CartProductDetailResponseDto(" +
        "c.product.productId," +
        "c.product.productName," +
        "c.product.imageUrl," +
        "c.product.price," +
        "c.quantity) " +
        "FROM Cart c " +
        "WHERE c.user.id = :userId")
    List<CartProductDetailResponseDto> findCartDtoListByUserId(@Param("userId") Long userId);

    /**
     * Deletes all cart entries associated with a specific user.
     *
     * @param userId the unique identifier of the user whose cart entries will be deleted
     * @throws DataAccessException if there is an error during the database deletion operation
     */
    @Modifying
    @Query("DELETE FROM Cart c WHERE c.user.id = :userId ")
    void deleteAllByUserId(@Param("userId") Long userId);

    /**
     * Retrieves a list of Cart entities with their associated Product information for a specific user.
     *
     * This method performs a join fetch to efficiently load Cart and Product data in a single query,
     * optimizing database access by avoiding the N+1 query problem.
     *
     * @param userId The unique identifier of the user whose cart items are to be retrieved
     * @return A list of Cart entities, each containing its associated Product details
     */
    @Query("SELECT c FROM Cart c JOIN FETCH c.product WHERE c.user.id = :userId")
    List<Cart> findCartsWithProductsByUserId(@Param("userId") Long userId);


}
