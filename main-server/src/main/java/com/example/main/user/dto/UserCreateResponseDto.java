package com.example.main.user.dto;

import com.example.common.user.entity.address.ShippingAddress;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class UserCreateResponseDto {

    private final Long userId;
    private final String name;
    private final String email;
    private final ShippingAddress address;
    private final LocalDateTime createdAt;

}
