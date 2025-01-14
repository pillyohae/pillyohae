package com.example.pillyohae.user.controller;

import com.example.pillyohae.order.dto.BuyerOrderDetailInfo;
import com.example.pillyohae.order.dto.BuyerOrderSearchResponseDto;
import com.example.pillyohae.order.service.OrderService;
import com.example.pillyohae.user.dto.*;
import com.example.pillyohae.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final OrderService orderService;

    @PostMapping("/signup")
    public ResponseEntity<UserCreateResponseDto> createUser(
        @Valid @RequestBody UserCreateRequestDto requestDto
    ) {
        UserCreateResponseDto responseDto = userService.createUser(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(
        @Valid @RequestBody UserLoginRequestDto requestDto
    ) {
        TokenResponse tokenResponse = userService.loginTokenGenerate(requestDto);

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken",
                tokenResponse.getRefreshToken())
            .httpOnly(true)
            .path("/")
            .secure(false)
            .maxAge(7 * 24 * 60 * 60)
            .build();

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenResponse.getAccessToken())
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
        @Valid @RequestBody UserDeleteRequestDto requestDto, Authentication authentication,
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
        @Valid @RequestBody UserProfileUpdateRequestDto requestDto,
        Authentication authentication
    ) {
        UserProfileResponseDto responseDto = userService.updateProfile(requestDto,
            authentication);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/orders")
    public ResponseEntity<BuyerOrderSearchResponseDto> findAllOrdersByBuyer(
        Authentication authentication,
        @RequestParam(name = "startAt") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startAt,
        @RequestParam(name = "endAt") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endAt,
        @RequestParam(name = "pageNumber", defaultValue = "0") @Min(0) Long pageNumber,
        @RequestParam(name = "pageSize", defaultValue = "10") @Min(1) @Max(100) Long pageSize
    ) {
        if (endAt.isBefore(startAt)) {
            throw new IllegalArgumentException("End date must be after start date");
        }
        return ResponseEntity.ok(orderService.findOrder(
            authentication.getName(),
            startAt,
            endAt,
            pageNumber,
            pageSize
        ));
    }

    // 결제된 order의 snapshot을 본다
    @GetMapping("/orders/{orderId}/orderItems")
    public ResponseEntity<BuyerOrderDetailInfo> findOrderDetailInfo(
        Authentication authentication, @PathVariable(name = "orderId") UUID orderId
    ) {
        return ResponseEntity.ok(orderService.getOrderDetailAfterPayment(authentication.getName(), orderId));
    }
}
