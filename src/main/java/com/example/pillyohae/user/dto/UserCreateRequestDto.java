package com.example.pillyohae.user.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserCreateRequestDto {
    private final String name;
    private final String email;
    private final String password;
    private final String address;
    private final String role;
}
