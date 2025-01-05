package com.example.pillyohae.global.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class JwtAuthResponse {

    private String tokenAuthScheme;

    private String accessToken;

    public JwtAuthResponse(String tokenAuthScheme, String accessToken) {
        this.tokenAuthScheme = tokenAuthScheme;
        this.accessToken = accessToken;
    }
}
