package com.example.pillyohae.refresh.controller;

import com.example.pillyohae.global.exception.CustomResponseStatusException;
import com.example.pillyohae.global.exception.code.ErrorCode;
import com.example.pillyohae.refresh.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RefreshTokenController {

    private final RefreshTokenService refreshTokenService;

    @PostMapping("/refresh")
    public ResponseEntity<Void> refresh(@CookieValue("refreshToken") String refreshToken) {
        try {
            // 리프레시 토큰으로 새로운 액세스 토큰 발급
            String newAccessToken = refreshTokenService.generateAccessTokenFromRefreshToken(
                refreshToken);

            return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + newAccessToken)
                .build();

        } catch (CustomResponseStatusException e) {
            // 리프레시 토큰이 만료되었거나 유효하지 않은 경우
            if (e.getErrorCode() == ErrorCode.BAD_REQUEST_TOKEN) {
                // 만료된 리프레시 토큰 삭제
                refreshTokenService.deleteRefreshToken(refreshToken);

                // 쿠키 삭제를 위해 만료시간을 0으로 설정
                ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", "")
                    .httpOnly(true)
                    .path("/")
                    .maxAge(0)
                    .build();

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                    .build();
            }
            throw e;
        }
    }
}
