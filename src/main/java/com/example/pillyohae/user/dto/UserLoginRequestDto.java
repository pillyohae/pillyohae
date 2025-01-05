package com.example.pillyohae.user.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserLoginRequestDto {

    private final String email;
    private final String password;
}
