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
     * Creates a new cart item for the authenticated user.
     *
     * @param userDetails The authenticated user's details used to identify the user creating the cart
     * @param requestDto The data transfer object containing the details of the product to be added to the cart
     * @return A ResponseEntity containing the created cart item details with HTTP 200 OK status
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
     * Retrieves the cart items for the authenticated user.
     *
     * @param userDetails The authentication details of the current user, used to identify the user's cart
     * @return A ResponseEntity containing the list of cart items for the user
     */
    @GetMapping
    public ResponseEntity<CartListResponseDto> findCart(@AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(cartService.findCart(userDetails.getUsername()));
    }

    /**
     * Updates the quantity of a specific item in the user's shopping cart.
     *
     * @param cartId The unique identifier of the cart item to be updated
     * @param userDetails The authentication details of the currently logged-in user
     * @param requestDto The data transfer object containing the new quantity for the cart item
     * @return A ResponseEntity containing the updated cart item details
     * @throws IllegalArgumentException If the cart item cannot be found or updated
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
     * Deletes a specific item from the user's cart.
     *
     * @param cartId The unique identifier of the cart item to be deleted
     * @param userDetails The authentication details of the current user
     * @return A ResponseEntity with HTTP status 200 OK upon successful deletion
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
     * Deletes all items from the user's shopping cart.
     *
     * @param userDetails The authenticated user's details used to identify the cart owner
     * @return A ResponseEntity with HTTP status 200 OK upon successful deletion of all cart items
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteAll(@AuthenticationPrincipal UserDetails userDetails) {
        cartService.deleteAll(userDetails.getUsername());
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
