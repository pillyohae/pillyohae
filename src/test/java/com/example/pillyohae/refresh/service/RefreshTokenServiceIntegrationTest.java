package com.example.pillyohae.refresh.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.example.pillyohae.global.exception.CustomResponseStatusException;
import com.example.pillyohae.global.util.JwtProvider;
import com.example.pillyohae.refresh.entity.RefreshToken;
import com.example.pillyohae.refresh.repository.RefreshTokenRepository;
import com.example.pillyohae.user.entity.User;
import com.example.pillyohae.user.entity.type.Role;
import com.example.pillyohae.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class RefreshTokenServiceIntegrationTest {

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private Authentication authentication;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        // 테스트용 사용자
        testUser = new User("홍길동", "test@example.com", "Abc1234!", "주소", Role.BUYER);
        // 사용자 DB에 저장
        testUser = userRepository.save(testUser);

        // Mock Authentication 객체가 user의 email을 반환하도록 when 설정
        when(authentication.getName()).thenReturn(testUser.getEmail());
    }

    @Test
    @DisplayName("만료된 리프레시 토큰 검증 시 DB 조회에서 예외 발생")
    void validRefreshToken_WithExpiredToken_ThrowsException() {
        // given
        // 리프레시 토큰을 생성
        String expiredToken = jwtProvider.generateRefreshToken(authentication);

        // 만료된 리프레시 토큰 객체 생성
        RefreshToken token = RefreshToken.builder()
            .userId(1L)
            .refreshToken(expiredToken)
            .expiresAt(LocalDateTime.now().minusDays(1))  // 현재 시간보다 하루 전
            .build();
        refreshTokenRepository.save(token);

        // when & then
        // 만료된 토큰을 검증하면 예외가 발생
        assertThatThrownBy(() -> refreshTokenService.validRefreshToken(token.getRefreshToken()))
            .isInstanceOf(CustomResponseStatusException.class);
    }

    @Test
    @DisplayName("리프레시 토큰으로부터 새로운 액세스 토큰 생성 성공")
    void generateAccessTokenFromRefreshToken_Success() {
        // given
        // 리프레시 토큰을 만들고 DB에 저장
        String refreshToken = jwtProvider.generateRefreshToken(authentication);
        refreshTokenService.saveRefreshToken(testUser.getId(), refreshToken);

        // when
        // 리프레시 토큰으로 액세스 토큰을 생성
        String newAccessToken = refreshTokenService.generateAccessTokenFromRefreshToken(
            refreshToken);

        // then
        assertThat(newAccessToken).isNotNull();  // 토큰이 null 이 아님을 확인
        assertThat(jwtProvider.validToken(newAccessToken)).isTrue();  // 액세스 토큰이 유효한지 확인
    }

    @Test
    @DisplayName("리프레시 토큰 저장 성공")
    void saveRefreshToken_Success() {
        // given
        String refreshToken = jwtProvider.generateRefreshToken(authentication);
        refreshTokenService.saveRefreshToken(testUser.getId(), refreshToken);

        // when & then
        // 저장한 리프레시 토큰 객체 조회
        Optional<RefreshToken> savedToken = refreshTokenRepository.findByUserId(testUser.getId());
        assertThat(savedToken).isPresent();  // 토큰이 실제로 저장되었는지 확인
        // 저장된 토큰이 원본과 같은지 확인
        assertThat(savedToken.get().getRefreshToken()).isEqualTo(refreshToken);
    }

    @Test
    @DisplayName("리프레시 토큰 삭제 성공")
    void deleteRefreshToken_Success() {
        // given
        String refreshToken = jwtProvider.generateRefreshToken(authentication);
        refreshTokenService.saveRefreshToken(testUser.getId(), refreshToken);

        // when
        // 저장된 토큰을 삭제
        refreshTokenService.deleteRefreshToken(refreshToken);

        // then
        // 삭제된 토큰을 검증하려고 하면 예외가 발생
        assertThatThrownBy(() -> refreshTokenService.validRefreshToken(refreshToken))
            .isInstanceOf(CustomResponseStatusException.class);
    }
}
