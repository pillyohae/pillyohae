package com.example.pillyohae.cart;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.example.pillyohae.cart.dto.CartListResponseDto;
import com.example.pillyohae.cart.dto.CartProductDetailResponseDto;
import com.example.pillyohae.cart.repository.CartRepository;
import com.example.pillyohae.cart.service.CartService;
import com.example.pillyohae.user.entity.User;
import com.example.pillyohae.user.entity.type.Role;
import com.example.pillyohae.user.service.UserService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private CartService cartService;

    @Test
    @DisplayName("장바구니 상품 목록 및 가격 조회")
    void testFindCart() {

        //Given
        User mockUser = new User("joon", "test@test.com", "1234", "", Role.BUYER);
        ReflectionTestUtils.setField(mockUser, "id", 1L);

        List<CartProductDetailResponseDto> mockProducts = List.of(
            new CartProductDetailResponseDto(null, null, null, null, 2000L, 3),
            new CartProductDetailResponseDto(null, null, null, null, 1000L, 1)
        );

        when(userService.findByEmail(mockUser.getEmail())).thenReturn(mockUser);
        when(cartRepository.findCartDtoListByUserId(mockUser.getId())).thenReturn(mockProducts);

        //When
        CartListResponseDto result = cartService.findCart(mockUser.getEmail());

        //Then
        assertEquals(mockUser.getId(), result.getUserId());
        assertEquals(2, result.getProducts().size());
        assertEquals(7000L, result.getTotalPrice());
    }

}
