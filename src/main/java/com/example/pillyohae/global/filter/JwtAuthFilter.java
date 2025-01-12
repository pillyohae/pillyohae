package com.example.pillyohae.global.filter;

import com.example.pillyohae.global.config.SecurityProperties;
import com.example.pillyohae.global.util.AuthenticationScheme;
import com.example.pillyohae.global.util.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    private final UserDetailsService userDetailsService;

    private final SecurityProperties securityProperties;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        // 화이트 리스트를 판단하기 위한 uri 와 method
        String requestUri = request.getRequestURI();
        String method = request.getMethod();

        // SecurityProperties에서 설정값들을 가져와서 사용
        if (securityProperties.getWhiteList().stream()
            .anyMatch(pattern -> pathMatcher.match(pattern, requestUri))) {
            filterChain.doFilter(request, response);
            return;
        }

        // HTTP Method 특정 패턴 체크, (GET /products/search 를 필터링 하지 않기위해 구현)
        Map<HttpMethod, List<String>> methodPatterns = securityProperties.getMethodSpecificPatterns();
        if (methodPatterns.containsKey(HttpMethod.valueOf(method))) {
            if (methodPatterns.get(HttpMethod.valueOf(method)).stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, requestUri))) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        this.authenticate(request);
        filterChain.doFilter(request, response);
    }

    private void authenticate(HttpServletRequest request) {

        String token = this.getTokenFromRequest(request);

        //받은 토큰 값이 유효한지 판단.
        if (!jwtProvider.validToken(token)) {
            return;
        }

        String username = jwtProvider.getUsername(token);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        this.setAuthentication(request, userDetails);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        //Bearer 를 포함한 토큰 String 값
        final String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        //"Bearer " 을 생성하는 과정. 이 String 값은 토큰 값만 추출해내는데 사용됨.
        final String headerPrefix = AuthenticationScheme.generateType(AuthenticationScheme.BEARER);

        //bearerToken 이 null 값이 아니고 "Bearer " 로 시작한다면 true 아니면 false
        boolean tokenFound =
            StringUtils.hasText(bearerToken) && bearerToken.startsWith(headerPrefix);

        //"Bearer " 을 분리시켜 토큰값만 String 값으로 return
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
