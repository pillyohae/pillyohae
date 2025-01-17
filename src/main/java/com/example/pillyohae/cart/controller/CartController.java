package com.example.pillyohae.cart.controller;

import com.example.pillyohae.cart.dto.CartCreateRequestDto;
import com.example.pillyohae.cart.dto.CartCreateResponseDto;
import com.example.pillyohae.cart.dto.CartListResponseDto;
import com.example.pillyohae.cart.dto.CartUpdateRequestDto;
import com.example.pillyohae.cart.dto.CartUpdateResponseDto;
import com.example.pillyohae.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/carts")
@RestController
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /**
     * 장바구니에 상품을 추가
     *
     * @param requestDto  추가할 상품 정보
     * @param userDetails 사용자 정보
     * @return 정상 처리 시 응답 DTO
     */
    @PostMapping
    public ResponseEntity<CartCreateResponseDto> createCart(
        @AuthenticationPrincipal UserDetails userDetails,
        @Valid @RequestBody CartCreateRequestDto requestDto
    ) {

        CartCreateResponseDto responseDto = cartService.createCart(requestDto, userDetails.getUsername());

        return ResponseEntity.ok(responseDto);
    }

    /**
     * 장바구니 목록을 조회
     *
     * @param userDetails 사용자 정보
     * @return 정상 처리 시 응답 DTO
     */
    @GetMapping
    public ResponseEntity<CartListResponseDto> findCart(@AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(cartService.findCart(userDetails.getUsername()));
    }

    /**
     * 장바구니에 담긴 상품 수량을 수정
     *
     * @param cartId      장바구니 ID
     * @param userDetails 사용자 정보
     * @param requestDto  수정할 수량 정보
     * @return 정상 처리 시 응답 DTO
     */
    @PutMapping("/{cartId}")
    public ResponseEntity<CartUpdateResponseDto> updateCart(
        @PathVariable Long cartId,
        @AuthenticationPrincipal UserDetails userDetails,
        @Valid @RequestBody CartUpdateRequestDto requestDto
    ) {

        return ResponseEntity.ok(cartService.updateCart(cartId, userDetails.getUsername(), requestDto));
    }

    /**
     * 장바구니에서 상품 삭제
     *
     * @param cartId      장바구니 ID
     * @param userDetails 사용자 정보
     * @return 정상 처리 시 응답 DTO
     */
    @DeleteMapping("/{cartId}")
    public ResponseEntity<Void> deleteCart(
        @PathVariable Long cartId,
        @AuthenticationPrincipal UserDetails userDetails
    ) {

        cartService.deleteCart(cartId, userDetails.getUsername());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 장바구니에서 전체 상품 삭제
     *
     * @param userDetails 사용자 정보
     * @return 정상 처리 시 200 OK
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteAll(@AuthenticationPrincipal UserDetails userDetails) {

        cartService.deleteAll(userDetails.getUsername());

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
