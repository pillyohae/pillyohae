package com.example.pillyohae.refresh.service;

import com.example.pillyohae.global.exception.CustomResponseStatusException;
import com.example.pillyohae.global.exception.code.ErrorCode;
import com.example.pillyohae.global.util.JwtProvider;
import com.example.pillyohae.refresh.entity.RefreshToken;
import com.example.pillyohae.refresh.repository.RefreshTokenRepository;
import com.example.pillyohae.user.entity.User;
import com.example.pillyohae.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final UserDetailsService userDetailsService;

    @Transactional
    public void saveRefreshToken(Long userId, String token) {
        // 기존 해당 유저의 리프레시 토큰이 있다면 삭제
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomResponseStatusException(ErrorCode.NOT_FOUND_USER));

        refreshTokenRepository.findByUserId(userId)
            .ifPresent(refreshTokenRepository::delete);

        RefreshToken refreshToken = RefreshToken.builder()
            .userId(userId)
            .refreshToken(token)
            .expiresAt(LocalDateTime.now().plusDays(7))
            .build();

        refreshTokenRepository.save(refreshToken);
    }

    public String generateAccessTokenFromRefreshToken(String refreshToken) {
        validRefreshToken(refreshToken);

        String email = jwtProvider.getUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());

        return jwtProvider.generateToken(authentication);
    }

    public void validRefreshToken(String refreshToken) {
        if (!jwtProvider.validateRefreshToken(refreshToken)) {
            throw new CustomResponseStatusException(ErrorCode.BAD_REQUEST_TOKEN);
        }
        RefreshToken token = refreshTokenRepository.findByRefreshToken(refreshToken)
            .orElseThrow(() -> new CustomResponseStatusException(ErrorCode.BAD_REQUEST_TOKEN));

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(token);
            throw new CustomResponseStatusException(ErrorCode.BAD_REQUEST_TOKEN);
        }
    }

    @Transactional
    public void deleteRefreshToken(String refreshToken) {
        refreshTokenRepository.deleteByRefreshToken(refreshToken);
    }
}
