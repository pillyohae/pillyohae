package com.example.pillyohae.user.controller;

import com.example.pillyohae.coupon.dto.CouponListResponseDto;
import com.example.pillyohae.coupon.dto.CouponTemplateListResponseDto;
import com.example.pillyohae.coupon.entity.CouponTemplate;
import com.example.pillyohae.coupon.service.CouponService;
import com.example.pillyohae.order.dto.OrderDetailResponseDto;
import com.example.pillyohae.order.dto.OrderInfoDto;
import com.example.pillyohae.order.dto.OrderSellerInfoDto;
import com.example.pillyohae.order.service.OrderService;
import com.example.pillyohae.refresh.service.RefreshTokenService;
import com.example.pillyohae.user.dto.*;
import com.example.pillyohae.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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

import static com.example.pillyohae.global.constant.TokenPrefix.TOKEN_PREFIX;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final OrderService orderService;
    private final CouponService couponService;

    /**
     * 사용자 회원가입
     *
     * @param requestDto 회원가입 관련 정보를 담고있는 요청 DTO
     * @return 정상 처리시 CREATED 상태코드, 사용자 기본 정보 반환
     */
    @PostMapping("/signup")
    public ResponseEntity<UserCreateResponseDto> createUser(
        @Valid @RequestBody UserCreateRequestDto requestDto
    ) {
        UserCreateResponseDto responseDto = userService.createUser(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    /**
     * 사용자 로그인
     *
     * @param requestDto 로그인 관련 정보를 담고있는 요청 DTO
     * @return 정상 처리시 헤더에 액세스토큰, 쿠키에 리프레시 토큰을 반환
     */
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
            .header(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + tokenResponse.getAccessToken())
            .build();
    }

    /**
     * 사용자 로그아웃
     *
     * @param request        HTTP 요청 정보를 담고있는 객체
     * @param response       HTTP 응답 정보를 담고있는 객체
     * @param authentication 토큰을 통해 얻어온 사용자 정보를 담고있는 인증 객체
     * @return 정상적으로 완료 시 OK 상태코드 반환
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication,
        @CookieValue(value = "refreshToken", required = false) String refreshToken,
        @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String accessToken
    ) {
        //인증 객체가 null 이 아니고 해당 객체가 인증된 상태라면
        if (authentication != null && authentication.isAuthenticated()) {
            try {
                // 1. SecurityContext 정리
                new SecurityContextLogoutHandler().logout(request, response, authentication);

                // 2. 리프레시 토큰 삭제
                if (refreshToken != null) {
                    refreshTokenService.deleteRefreshToken(authentication);
                }

                // 3. 액세스 토큰 블랙리스트 추가 Authorization header 에 담긴 값이 Bearer 로 시작할 경우
                if (accessToken != null && accessToken.startsWith(TOKEN_PREFIX)) {
                    refreshTokenService.addToBlacklist(accessToken);
                }

                // 4. 리프레시 토큰 쿠키 만료
                ResponseCookie expiredCookie = ResponseCookie.from("refreshToken", "")
                    .httpOnly(true)
                    .path("/")
                    .maxAge(0)
                    .secure(false)
                    .build();

                return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, expiredCookie.toString())
                    .build();
            } catch (Exception ex) {
                log.error("로그아웃 실패", ex);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }

        //로그인 한 상태가 아니라면
        throw new UsernameNotFoundException("로그인이 먼저 필요합니다.");
    }

    /**
     * 사용자 삭제
     *
     * @param requestDto     비밀번호를 담고있는 요청 DTO
     * @param authentication 토큰을 통해 얻어온 사용자 정보를 담고있는 인증 객체
     * @param request        HTTP 요청 정보를 담고있는 객체
     * @param response       HTTP 응답 정보를 담고있는 객체
     * @return 정상적으로 완료 시 OK 상태코드 반환
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteUser(
        @Valid @RequestBody UserDeleteRequestDto requestDto, Authentication authentication,
        HttpServletRequest request, HttpServletResponse response
    ) {
        userService.deleteUser(requestDto, authentication);

        new SecurityContextLogoutHandler().logout(request, response, authentication);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 사용자 프로필 조회
     *
     * @param authentication 토큰을 통해 얻어온 사용자 정보를 담고있는 인증 객체
     * @return 정상적으로 완료 시 사용자 기본 정보를 반환
     */
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponseDto> getProfile(
        Authentication authentication
    ) {

        return new ResponseEntity<>(userService.getProfile(authentication), HttpStatus.OK);
    }

    /**
     * 사용자 프로필 정보 수정 : 이름, 주소, 패스워드 수정 가능
     *
     * @param requestDto     수정할 데이터의 정보가 담겨있는 DTO
     * @param authentication 토큰을 통해 얻어온 사용자 정보를 담고있는 인증 객체
     * @return 정상적으로 완료 시 OK 상태코드와 수정된 정보를 반환
     */
    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponseDto> updateProfile(
        @Valid @RequestBody UserProfileUpdateRequestDto requestDto,
        Authentication authentication
    ) {
        UserProfileResponseDto responseDto = userService.updateProfile(requestDto,
            authentication);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    /**
     * 사용자 주문 조회
     *
     * @param authentication 토큰을 통해 얻어온 사용자 정보를 담고있는 인증 객체
     * @param startAt        조회 기준 시작 날짜
     * @param endAt          조회 기준 끝 날자
     * @param pageNumber     조회할 페이지
     * @param pageSize       조회할 페이지의 크기
     * @return 정상적으로 완료시 OK 상태코드와 주문 정보를 반환
     */
    @GetMapping("/orders")
    public ResponseEntity<Page<OrderInfoDto>> findAllOrdersByBuyer(
        Authentication authentication,
        @RequestParam(name = "startAt", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startAt,
        @RequestParam(name = "endAt", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endAt,
        @RequestParam(name = "pageNumber", defaultValue = "0") @Min(0) Integer pageNumber,
        @RequestParam(name = "pageSize", defaultValue = "10") @Min(1) @Max(100) Integer pageSize
    ) {
        return ResponseEntity.ok(orderService.findOrders(
            authentication.getName(),
            startAt,
            endAt,
            pageNumber,
            pageSize
        ));
    }

    /**
     * 사용자 주문 상세 조회
     *
     * @param authentication 토큰을 통해 얻어온 사용자 정보를 담고있는 인증 객체
     * @param orderId        주문 식별자
     * @return 정상적으로 완료시 OK 상태코드와 주문 상세 정보를 반환
     */
    @GetMapping("/orders/{orderId}/orderItems")
    public ResponseEntity<OrderDetailResponseDto> findOrderDetail(
        Authentication authentication, @PathVariable(name = "orderId") UUID orderId
    ) {
        return ResponseEntity.ok(
            orderService.findOrderDetail(authentication.getName(), orderId));
    }

    /**
     * 유저의 쿠폰 조회
     *
     * @param authentication 토큰을 통해 얻어온 사용자 정보를 담고있는 인증 객체
     * @param totalPrice     주문에 사용할 쿠폰 조회시 현재 주문 총 금액
     * @return 정상적으로 완료시 OK 상태코드와 사용 가능한 쿠폰 목록 정보를 반환
     */
    @GetMapping("/coupons")
    public ResponseEntity<CouponListResponseDto> getCouponListToUse(Authentication authentication, @RequestParam(required = false) Long totalPrice) {
        return ResponseEntity.ok(couponService.findCouponListToUse(authentication.getName(), totalPrice));
    }

    /**
     * 판매자의 주문 조회
     *
     * @param authentication
     * @param startAt
     * @param endAt
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @GetMapping("/sellers/orders")
    public ResponseEntity<Page<OrderSellerInfoDto>> findAllSellerOrders(
        Authentication authentication,
        @RequestParam(name = "startAt", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startAt,
        @RequestParam(name = "endAt", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endAt,
        @RequestParam(name = "pageNumber", defaultValue = "0") @Min(0) Integer pageNumber,
        @RequestParam(name = "pageSize", defaultValue = "10") @Min(1) @Max(100) Integer pageSize
    ) {
        return ResponseEntity.ok(orderService.findSellerOrders(
            authentication.getName(),
            startAt,
            endAt,
            pageNumber,
            pageSize
        ));
    }

    // 상태에 따른 쿠폰 조회 (관리자만 조회 가능)
    @GetMapping("/admin/coupons")
    public ResponseEntity<CouponTemplateListResponseDto> getAvailableCoupons(Authentication authentication,
                                                                             @RequestParam(required = false) CouponTemplate.CouponStatus couponStatus) {
        return ResponseEntity.ok(couponService.findCouponList(couponStatus));
    }

}
