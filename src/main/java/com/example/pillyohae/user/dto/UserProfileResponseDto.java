package com.example.pillyohae.user.dto;

import com.example.pillyohae.global.entity.address.ShippingAddress;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserProfileResponseDto {

    private final Long userId;
    private final String name;
    private final String email;
    private final ShippingAddress address;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
