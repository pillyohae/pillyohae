package com.example.pillyohae.global.interceptor;

import com.example.pillyohae.global.constants.GlobalConstants;
import com.example.pillyohae.global.dto.Authentication;
import com.example.pillyohae.global.entity.Role;
import com.example.pillyohae.global.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminRoleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws UnauthorizedException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new UnauthorizedException(HttpStatus.UNAUTHORIZED, "세션이 끊어졌습니다.");
        }

        Authentication authentication = (Authentication) session.getAttribute(GlobalConstants.USER_AUTH);
        Role role = authentication.getRole();

        if (!Role.ADMIN.equals(role)) {
            throw new UnauthorizedException(HttpStatus.UNAUTHORIZED, "admin 권한이 필요합니다.");
        }

        return true;
    }

}
