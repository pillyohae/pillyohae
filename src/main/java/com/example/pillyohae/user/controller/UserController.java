package com.example.pillyohae.user.controller;

import com.example.pillyohae.global.dto.JwtAuthResponse;
import com.example.pillyohae.user.dto.UserCreateRequestDto;
import com.example.pillyohae.user.dto.UserCreateResponseDto;
import com.example.pillyohae.user.dto.UserDeleteRequestDto;
import com.example.pillyohae.user.dto.UserLoginRequestDto;
import com.example.pillyohae.user.dto.UserProfileResponseDto;
import com.example.pillyohae.user.dto.UserProfileUpdateRequestDto;
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

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginPage() {
      return "login";
    }

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

        return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .build();
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

    @DeleteMapping
    public ResponseEntity<Void> deleteUser(
        @RequestBody UserDeleteRequestDto requestDto, Authentication authentication,
        HttpServletRequest request, HttpServletResponse response
    ) {
        userService.deleteUser(requestDto, authentication);

        new SecurityContextLogoutHandler().logout(request, response, authentication);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponseDto> getProfile(
        Authentication authentication
    ) {

        return new ResponseEntity<>(userService.getProfile(authentication), HttpStatus.OK);
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponseDto> updateProfile(
        @RequestBody UserProfileUpdateRequestDto requestDto,
        Authentication authentication
    ) {
        UserProfileResponseDto responseDto = userService.updateProfile(requestDto,
            authentication);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
