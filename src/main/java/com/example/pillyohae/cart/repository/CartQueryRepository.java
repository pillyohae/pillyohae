package com.example.pillyohae.cart.repository;

import com.example.pillyohae.cart.dto.CartProductDetailResponseDto;
import java.util.List;

public interface CartQueryRepository {

    List<CartProductDetailResponseDto> findCartDtoListByUserId(Long userId);
}
