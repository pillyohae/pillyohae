package com.example.pillyohae.refresh.service;

import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RefreshTokenServiceIntegrationTest {

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Test
    @DisplayName("블랙리스트 조회 성능 테스트 - 동일한 토큰 반복 조회")
    void blacklistLookupPerformanceTest() {
        // 테스트용 토큰 생성
        String testToken = "test_access_token";

        // 블랙리스트에 추가
        refreshTokenService.addToBlacklist("Bearer " + testToken);

        // 워밍업
        for (int i = 0; i < 1000; i++) {
            refreshTokenService.isTokenBlacklisted(testToken);
        }

        // 실제 성능 측정
        long startTime = System.nanoTime();
        for (int i = 0; i < 100000; i++) {
            refreshTokenService.isTokenBlacklisted(testToken);
        }
        long endTime = System.nanoTime();

        long durationInMillis = (endTime - startTime) / 1_000_000;
        System.out.println("평균 조회 시간: " + (durationInMillis / 10000.0) + "ms");
    }

    @Test
    @DisplayName("블랙리스트 조회 성능 테스트 - 서로 다른 토큰 조회")
    void blacklistLookupPerformanceTestDifferentTokens() {
        // 여러 개의 테스트 토큰 생성 및 블랙리스트 추가
        List<String> tokens = IntStream.range(0, 10000)
            .mapToObj(i -> "test_token_" + i)
            .toList();

        tokens.forEach(token ->
            refreshTokenService.addToBlacklist("Bearer " + token));

        // 성능 측정
        long startTime = System.nanoTime();
        tokens.forEach(token ->
            refreshTokenService.isTokenBlacklisted(token));
        long endTime = System.nanoTime();

        long durationInMillis = (endTime - startTime) / 1_000_000;
        System.out.println("평균 조회 시간: " + (durationInMillis / 1000.0) + "ms");
    }

}
