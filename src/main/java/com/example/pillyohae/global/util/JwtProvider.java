package com.example.pillyohae.global.util;

import com.example.pillyohae.user.entity.User;
import com.example.pillyohae.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.persistence.EntityNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {

  /**
   * JWT 시크릿 키.
   */
  @Value("${jwt.secret}")
  private String secret;

  /**
   * 토큰 만료시간(밀리초).
   */
  @Getter
  @Value("${jwt.expiry-millis}")
  private long expiryMillis;

  private final UserRepository userRepository;

  public String generateToken(Authentication authentication) throws EntityNotFoundException {
    String username = authentication.getName();
    return this.generateTokenBy(username);
  }


  public String getUsername(String token) {
    Claims claims = this.getClaims(token);
    return claims.getSubject();
  }

  public boolean validToken(String token) throws JwtException {
    try {
      return !this.tokenExpired(token);
    } catch (MalformedJwtException e) {
      log.error("Invalid JWT token: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
      log.error("JWT token is expired: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
      log.error("JWT token is unsupported: {}", e.getMessage());
    }

    return false;
  }

  private String generateTokenBy(String email) throws EntityNotFoundException {
    User user = this.userRepository.findByEmail(email)
        .orElseThrow(() -> new EntityNotFoundException("해당 email에 맞는 값이 존재하지 않습니다."));
    Date currentDate = new Date();
    Date expireDate = new Date(currentDate.getTime() + this.expiryMillis);

    return Jwts.builder()
        .subject(email)
        .issuedAt(currentDate)
        .expiration(expireDate)
        .claim("role", user.getRole())
        .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)), Jwts.SIG.HS256)
        .compact();
  }

  private Claims getClaims(String token) {
    if (!StringUtils.hasText(token)) {
      throw new MalformedJwtException("토큰이 비어 있습니다.");
    }

    return Jwts.parser()
        .verifyWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  private boolean tokenExpired(String token) {
    final Date expiration = this.getExpirationDateFromToken(token);
    return expiration.before(new Date());
  }

  private Date getExpirationDateFromToken(String token) {
    return this.resolveClaims(token, Claims::getExpiration);
  }

  private <T> T resolveClaims(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = this.getClaims(token);
    return claimsResolver.apply(claims);
  }
}
