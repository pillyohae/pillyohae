package com.example.pillyohae.cart.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.example.pillyohae.cart.dto.CartCreateRequestDto;
import com.example.pillyohae.cart.dto.CartCreateResponseDto;
import com.example.pillyohae.cart.dto.CartListResponseDto;
import com.example.pillyohae.cart.dto.CartProductDetailResponseDto;
import com.example.pillyohae.cart.dto.CartUpdateRequestDto;
import com.example.pillyohae.cart.dto.CartUpdateResponseDto;
import com.example.pillyohae.cart.entity.Cart;
import com.example.pillyohae.cart.repository.CartRepository;
import com.example.pillyohae.product.entity.Product;
import com.example.pillyohae.product.service.ProductService;
import com.example.pillyohae.user.entity.User;
import com.example.pillyohae.user.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final UserService userService;
    private final ProductService productService;


    /**
     * 장바구니에 상품을 추가
     *
     * @param requestDto 추가할 상품 정보
     * @param email      사용자 이메일
     * @return 정상 처리 시 응답 DTO
     */
    @Transactional
    public CartCreateResponseDto createCart(CartCreateRequestDto requestDto, String email) {

        User findUser = userService.findByEmail(email);

        Product findProduct = productService.findById(requestDto.getProductId());

        Cart cart = new Cart(findUser, findProduct, requestDto.getQuantity());

        cartRepository.save(cart);

        return new CartCreateResponseDto(cart.getId(), cart.getCreatedAt());
    }

    /**
     * Retrieves the cart items for a specific user.
     *
     * This method finds all cart products associated with the user and calculates the total cart value.
     * It requires a valid user email to fetch the cart details.
     *
     * @param email The email address of the user whose cart is being retrieved
     * @return A CartListResponseDto containing the total cart price and a list of cart product details
     * @throws UserNotFoundException if no user is found with the provided email
     */
    public CartListResponseDto findCart(String email) {

        User user = userService.findByEmail(email);

        List<CartProductDetailResponseDto> products = cartRepository.findCartDtoListByUserId(user.getId());

        Long totalPrice = products.stream()
            .mapToLong(product -> product.getPrice() * product.getQuantity())
            .sum();

        return new CartListResponseDto(totalPrice, products);
    }

    /**
     * Updates the quantity of a product in a user's cart.
     *
     * This method allows modifying the quantity of a specific cart item. It performs validation
     * to ensure that the cart belongs to the requesting user before updating the quantity.
     *
     * @param cartId     The unique identifier of the cart item to be updated
     * @param email      The email of the user requesting the cart update
     * @param requestDto Data transfer object containing the new quantity for the cart item
     * @return CartUpdateResponseDto containing the updated product ID and new quantity
     * @throws NotFoundException if the cart item cannot be found
     * @throws AccessDeniedException if the cart does not belong to the requesting user
     */
    @Transactional
    public CartUpdateResponseDto updateCart(
        Long cartId,
        String email,
        CartUpdateRequestDto requestDto
    ) throws NotFoundException, AccessDeniedException {

        User user = userService.findByEmail(email);

        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "장바구니에서 상품을 찾을 수 없습니다."));

        if (!cart.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("권한이 없습니다.");
        }

        cart.updateQuantity(requestDto.getQuantity());

        return new CartUpdateResponseDto(cart.getProduct().getProductId(), cart.getQuantity());
    }

    /**
     * Deletes a specific item from the user's cart after verifying user ownership.
     *
     * @param cartId The unique identifier of the cart item to be deleted
     * @param email The email of the user attempting to delete the cart item
     * @throws AccessDeniedException If the cart does not belong to the specified user
     * @throws ResponseStatusException If the cart item cannot be found in the repository
     */
    @Transactional
    public void deleteCart(Long cartId, String email) throws AccessDeniedException {

        User user = userService.findByEmail(email);

        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "장바구니에서 상품을 찾을 수 없습니다."));

        if (!cart.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("권한이 없습니다.");
        }

        cartRepository.delete(cart);
    }

    /**
     * Retrieves all cart items for a specific user by their user ID.
     *
     * @param userId The unique identifier of the user whose cart items are to be retrieved
     * @return A list of Cart objects associated with the specified user
     * @throws NotFoundException if no cart items are found for the given user ID
     */
    public List<Cart> findByUserId(Long userId) {

        List<Cart> carts = cartRepository.findCartsWithProductsByUserId(userId);

        if (carts.isEmpty()) {
            throw new NotFoundException("장바구니가 비어있습니다.");
        }

        return carts;
    }

    /**
     * Deletes all cart items for a specific user.
     *
     * This method removes all products from the user's shopping cart based on their email address.
     * It first retrieves the user by email and then deletes all cart entries associated with that user.
     *
     * @param email the email address of the user whose cart items will be completely removed
     * @throws UserNotFoundException if no user is found with the provided email
     */
    @Transactional
    public void deleteAll(String email) {

        User findUser = userService.findByEmail(email);

        cartRepository.deleteAllByUserId(findUser.getId());
    }
}
