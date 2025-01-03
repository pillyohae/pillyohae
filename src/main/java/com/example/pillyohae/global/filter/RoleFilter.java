package com.example.pillyohae.global.filter;

import com.example.pillyohae.global.constants.GlobalConstants;
import com.example.pillyohae.global.dto.Authentication;
import com.example.pillyohae.global.entity.Role;
import com.example.pillyohae.global.exception.UnauthorizedException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.io.IOException;

@RequiredArgsConstructor
public class RoleFilter implements CommonAuthFilter {

    private final Role role;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpSession session = findHttpSession(servletRequest);

        Authentication authentication = (Authentication) session.getAttribute(GlobalConstants.USER_AUTH);

        Role userRole = authentication.getRole();
        if(userRole != this.role) {
            throw new UnauthorizedException(HttpStatus.UNAUTHORIZED, role.getName() + "권한이 필요합니다.");
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
