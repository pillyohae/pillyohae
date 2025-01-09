package com.example.pillyohae.global.filter;

import com.example.pillyohae.global.util.AuthenticationScheme;
import com.example.pillyohae.global.util.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    private final UserDetailsService userDetailsService;

    // 화이트리스트 경로 정의
    private static final List<String> WHITE_LIST = List.of("/users/login", "/users/signup", "/users/products/**", "/toss/fail", "/toss/success", "/toss/confirm", "/products");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        String requestUri = request.getRequestURI();

        // 화이트리스트 경로인 경우 필터링을 건너뜀
        if (!WHITE_LIST.contains(requestUri)) {
            this.authenticate(request);
        }


        filterChain.doFilter(request, response);
    }

    private void authenticate(HttpServletRequest request) {

        String token = this.getTokenFromRequest(request);

        if (!jwtProvider.validToken(token)) {
            return;
        }

        String username = jwtProvider.getUsername(token);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        this.setAuthentication(request, userDetails);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        final String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String headerPrefix = AuthenticationScheme.generateType(AuthenticationScheme.BEARER);

        boolean tokenFound =
            StringUtils.hasText(bearerToken) && bearerToken.startsWith(headerPrefix);

        if (tokenFound) {
            return bearerToken.substring(headerPrefix.length());
        }

        return null;
    }

    private void setAuthentication(HttpServletRequest request, UserDetails userDetails) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
