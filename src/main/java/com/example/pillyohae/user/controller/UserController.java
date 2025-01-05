package com.example.pillyohae.user.controller;

import com.example.pillyohae.global.dto.JwtAuthResponse;
import com.example.pillyohae.user.dto.UserCreateRequestDto;
import com.example.pillyohae.user.dto.UserCreateResponseDto;
import com.example.pillyohae.user.dto.UserLoginRequestDto;
import com.example.pillyohae.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserCreateResponseDto> createUser(
        @RequestBody UserCreateRequestDto requestDto
    ) {
        UserCreateResponseDto responseDto = userService.createUser(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(
        @RequestBody UserLoginRequestDto requestDto
    ) {
        String accessToken = userService.loginTokenGenerate(requestDto);

        return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request,
        HttpServletResponse response, Authentication authentication) {

        if (authentication != null && authentication.isAuthenticated()) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);

            return new ResponseEntity<>(HttpStatus.OK);
        }

        throw new UsernameNotFoundException("로그인이 먼저 필요합니다.");
    }
}
