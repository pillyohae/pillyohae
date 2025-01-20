package com.example.pillyohae.cart;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.example.pillyohae.cart.dto.CartListResponseDto;
import com.example.pillyohae.cart.dto.CartProductDetailResponseDto;
import com.example.pillyohae.cart.repository.CartRepository;
import com.example.pillyohae.cart.service.CartService;
import com.example.pillyohae.global.entity.address.ShippingAddress;
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

    private static class TestUser {

        private static final String NAME = "joon";
        private static final String EMAIL = "test@test.com";
        private static final String PASSWORD = "1234";
        private static final ShippingAddress ADDRESS = new ShippingAddress("TestUser","010-0000-0000","test-zip","test-road","100-100");
        private static final Role ROLE = Role.BUYER;
        private static final Long ID = 1L;
    }

    private static class TestProducts {

        private static final Long PRICE1 = 2000L;
        private static final int QUANTITY1 = 3;
        private static final Long PRICE2 = 1000L;
        private static final int QUANTITY2 = 1;
    }

    private static final int EXPECTED_PRODUCT_COUNT = 2;
    private static final Long EXPECTED_TOTAL_PRICE = 7000L;

    @Test
    @DisplayName("장바구니 상품 목록 및 가격 조회")
    void testFindCart() {

        //Given
        User mockUser = new User(TestUser.NAME, TestUser.EMAIL, TestUser.PASSWORD, TestUser.ADDRESS, TestUser.ROLE);
        ReflectionTestUtils.setField(mockUser, "id", TestUser.ID);

        List<CartProductDetailResponseDto> mockProducts = List.of(
            CartProductDetailResponseDto.builder().price(TestProducts.PRICE1).quantity(TestProducts.QUANTITY1).build(),
            CartProductDetailResponseDto.builder().price(TestProducts.PRICE2).quantity(TestProducts.QUANTITY2).build()
        );

        when(userService.findByEmail(mockUser.getEmail())).thenReturn(mockUser);
        when(cartRepository.findCartDtoListByUserId(mockUser.getId())).thenReturn(mockProducts);

        //When
        CartListResponseDto result = cartService.findCart(mockUser.getEmail());

        //Then
        assertEquals(mockUser.getId(), result.getUserId());
        assertEquals(EXPECTED_PRODUCT_COUNT, result.getProducts().size());
        assertEquals(EXPECTED_TOTAL_PRICE, result.getTotalPrice());
    }

}
