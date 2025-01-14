package com.example.pillyohae.refresh.repository;

import com.example.pillyohae.refresh.entity.RefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    Optional<RefreshToken> findByUserId(Long userId);

    void deleteByRefreshToken(String refreshToken);
}
