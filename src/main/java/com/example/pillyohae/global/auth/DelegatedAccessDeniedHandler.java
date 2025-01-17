package com.example.pillyohae.global.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

/**
 * Spring Security의 {@link AccessDeniedHandler}를 구현한 커스텀 클래스입니다.
 *
 * <p>
 * 사용자가 인증은 되었지만 요청한 자원에 접근 권한이 없을 경우 발생하는 {@link AccessDeniedException}을 처리합니다.
 * </p>
 *
 * <p>
 * 이 클래스는 Spring의 {@link HandlerExceptionResolver}를 사용하여 예외 처리를 위임하므로, 애플리케이션 전반에서 일관된 방식으로 에러를 처리할
 * 수 있습니다.
 * </p>
 *
 * <p>
 * 예시: Spring Security 설정에서 이 핸들러를 등록하여 사용합니다.
 * </p>
 *
 * <pre>
 * http.exceptionHandling()
 *     .accessDeniedHandler(delegatedAccessDeniedHandler);
 * </pre>
 *
 * @author Weseunghyun
 * @since 1.0
 */
@Component
public class DelegatedAccessDeniedHandler implements AccessDeniedHandler {

    private final HandlerExceptionResolver resolver;

    /**
     * {@code DelegatedAccessDeniedHandler} 생성자.
     *
     * @param resolver {@link HandlerExceptionResolver}를 주입받아 예외 처리를 위임
     */
    public DelegatedAccessDeniedHandler(
        @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.resolver = resolver;
    }

    /**
     * 접근 권한이 없는 요청을 처리합니다.
     *
     * @param request               요청 객체로, 문제가 발생한 HTTP 요청 정보
     * @param response              응답 객체로, 클라이언트로 보낼 정보
     * @param accessDeniedException 접근 권한이 없을 때 발생하는 예외 객체
     * @throws IOException      입출력 관련 예외가 발생한 경우.
     * @throws ServletException 서블릿 처리 중 예외가 발생한 경우.
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
        AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // HandlerExceptionResolver를 통해 예외를 처리하도록 위임합니다.
        resolver.resolveException(request, response, null, accessDeniedException);
    }
}
