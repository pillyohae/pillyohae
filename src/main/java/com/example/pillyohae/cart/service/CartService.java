package com.example.pillyohae.cart.service;

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

        Cart inCart = cartRepository.findByProduct_ProductIdAndUserId(requestDto.getProductId(), findUser.getId());

        if (inCart != null) {
            inCart.updateQuantity(inCart.getQuantity() + requestDto.getQuantity());
            return new CartCreateResponseDto(inCart.getId(), inCart.getCreatedAt());
        }

        Cart cart = new Cart(findUser, findProduct, requestDto.getQuantity());

        cartRepository.save(cart);

        return new CartCreateResponseDto(cart.getId(), cart.getCreatedAt());
    }

    /**
     * 장바구니 목록을 조회
     *
     * @param email 사용자 이메일
     * @return 정상 처리 시 응답 DTO
     */
    public CartListResponseDto findCart(String email) {

        User user = userService.findByEmail(email);

        List<CartProductDetailResponseDto> products = cartRepository.findCartDtoListByUserId(user.getId());

        Long totalPrice = products.stream()
            .mapToLong(product -> product.getPrice() * product.getQuantity())
            .sum();

        return new CartListResponseDto(user.getId(), totalPrice, products);
    }

    /**
     * 장바구니 상품 수량을 수정 요청 사용자 정보와 카트 정보가 일치하지 않으면 예외 발생
     *
     * @param cartId     카트 아이디
     * @param email      사용자 이메일
     * @param requestDto 수정할 수량
     * @return 정상 처리 시 응답 DTO
     */
    @Transactional
    public CartUpdateResponseDto updateCart(Long cartId, String email, CartUpdateRequestDto requestDto) {

        User user = userService.findByEmail(email);

        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "장바구니에서 상품을 찾을 수 없습니다."));

        if (!cart.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }

        cart.updateQuantity(requestDto.getQuantity());

        return new CartUpdateResponseDto(cart.getProduct().getProductId(), cart.getQuantity());
    }

    /**
     * 장바구니 상품 삭제 요청 사용자 정보와 카트 정보가 일치하지 않으면 예외 발생
     *
     * @param cartId 카트 아이디
     * @param email  사용자 이메일
     */
    @Transactional
    public void deleteCart(Long cartId, String email) {

        User user = userService.findByEmail(email);

        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "장바구니에서 상품을 찾을 수 없습니다."));

        if (!cart.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }

        cartRepository.delete(cart);
    }

    /**
     * 사용자의 장바구니 목록 조회
     *
     * @param userId 사용자 ID
     * @return 장바구니 목록
     */
    public List<Cart> findByUserId(Long userId) {

        List<Cart> carts = cartRepository.findCartsWithProductsByUserId(userId);

        if (carts.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "장바구니가 비어있습니다.");
        }

        return carts;
    }

    /**
     * 사용자의 장바구니 내 상품을 전부 제거
     *
     * @param email 사용자 이메일
     */
    @Transactional
    public void deleteAll(String email) {

        User user = userService.findByEmail(email);

        cartRepository.deleteByUserId(user.getId());
    }
}
