package com.example.main.cart.repository;

import com.example.main.cart.dto.CartProductDetailResponseDto;
import java.util.List;

public interface CartQueryRepository {

    List<CartProductDetailResponseDto> findCartDtoListByUserId(Long userId);
}
