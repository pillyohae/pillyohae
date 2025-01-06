package com.example.pillyohae.user.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserProfileResponseDto {

    private final Long userId;
    private final String name;
    private final String email;
    private final String address;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
