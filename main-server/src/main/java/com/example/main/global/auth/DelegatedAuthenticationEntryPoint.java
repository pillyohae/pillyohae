package com.example.main.global.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

/**
 * Spring Security 의 {@link AuthenticationEntryPoint} 를 구현한 클래스입니다.
 *
 * <p>
 * 사용자가 인증에 실패했을 때 발생하는 {@link AuthenticationException} 을 처리합니다
 * </p>
 *
 * <p>
 * 이 클래스는 Spring의 {@link HandlerExceptionResolver}를 사용하여 예외 처리를 위임하므로, 애플리케이션 전반에서 일관된 방식으로 에러를 처리할
 * 수 있습니다.
 * </p>
 *
 * <p>
 * 예시: Spring Security 설정에서 이 엔트리 포인트를 등록하여 사용합니다.
 * </p>
 *
 * <pre>
 *   http.exceptionHandling()
 *       .authenticationEntryPoint(delegatedAuthenticationEntryPoint);
 * </pre>
 *
 * @author Weseunghyun
 * @since 1.0
 */
@Component
public class DelegatedAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final HandlerExceptionResolver resolver;

    /**
     * {@code DelegatedAuthenticationEntryPoint} 생성자.
     *
     * @param resolver {@link HandlerExceptionResolver}를 주입받아 예외 처리를 위임
     */
    public DelegatedAuthenticationEntryPoint(
        @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.resolver = resolver;
    }

    /**
     * 인증되지 않은 요청을 처리합니다.
     *
     * @param request       요청 객체로, 문제가 발생한 HTTP 요청 정보
     * @param response      응답 객체로, 클라이언트로 보낼 정보
     * @param authException 인증되지 않은 경우 발생하는 예외 객체
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException authException) {
        resolver.resolveException(request, response, null, authException);
    }
}
