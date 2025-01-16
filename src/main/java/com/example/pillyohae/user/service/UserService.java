package com.example.pillyohae.user.service;

import com.example.pillyohae.global.exception.CustomResponseStatusException;
import com.example.pillyohae.global.exception.code.ErrorCode;
import com.example.pillyohae.global.util.JwtProvider;
import com.example.pillyohae.refresh.service.RefreshTokenService;
import com.example.pillyohae.user.dto.TokenResponse;
import com.example.pillyohae.user.dto.UserCreateRequestDto;
import com.example.pillyohae.user.dto.UserCreateResponseDto;
import com.example.pillyohae.user.dto.UserDeleteRequestDto;
import com.example.pillyohae.user.dto.UserLoginRequestDto;
import com.example.pillyohae.user.dto.UserProfileResponseDto;
import com.example.pillyohae.user.dto.UserProfileUpdateRequestDto;
import com.example.pillyohae.user.entity.User;
import com.example.pillyohae.user.entity.type.Role;
import com.example.pillyohae.user.entity.type.Status;
import com.example.pillyohae.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    /**
     * 사용자 회원가입으로 인한 유저 생성
     *
     * @param requestDto 유저 생성에 필요한 정보가 담긴 DTO
     * @return 정상 완료 시 클라이언트에게 반환할 responseDto
     * @throws DuplicateKeyException 이메일이 중복되었을 때 발생하는 예외
     */
    public UserCreateResponseDto createUser(UserCreateRequestDto requestDto)
        throws DuplicateKeyException {

        boolean duplicated = userRepository.findByEmail(requestDto.getEmail()).isPresent();

        if (duplicated) {
            throw new DuplicateKeyException("중복된 이메일 입니다.");
        }

        User user = new User(requestDto.getName(), requestDto.getEmail(),
            passwordEncoder.encode(requestDto.getPassword()), requestDto.getAddress(),
            Role.of(requestDto.getRole()));

        User savedUser = userRepository.save(user);

        return new UserCreateResponseDto(savedUser.getId(), savedUser.getName(),
            savedUser.getEmail(), savedUser.getAddress(), savedUser.getCreatedAt());
    }

    /**
     * 로그인으로 인해 액세스 토큰, 리프레시 토큰 생성 후 반환
     *
     * @param requestDto 로그인에 필요한 사용자 정보가 담긴 DTO
     * @return 정상적으로 완료 시 액세스 토큰, 리프레시 토큰이 담긴 DTO 객체 반환
     * @throws CustomResponseStatusException 탈퇴한 사용자가 로그인할 시 반환되는 예외
     */
    public TokenResponse loginTokenGenerate(UserLoginRequestDto requestDto)
        throws CustomResponseStatusException {

        User user = findByEmail(requestDto.getEmail());

        if (user.getStatus() == Status.WITHDRAW) {
            throw new CustomResponseStatusException(ErrorCode.FORBIDDEN_DELETED_USER_LOGIN);
        }

        // 이 과정에서 Provider 가 인증 처리를 진행 (사용자 정보 조회, 비밀번호 검증)
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                requestDto.getEmail(),
                requestDto.getPassword()
            )
        );

        // 인증 객체를 SecurityContext에 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // access, refresh 토큰 생성 후 반환
        String accessToken = jwtProvider.generateAccessToken(authentication);
        String refreshToken = jwtProvider.generateRefreshToken(authentication);

        refreshTokenService.saveRefreshToken(user.getId(), refreshToken);

        return new TokenResponse(accessToken, refreshToken);
    }


    /**
     * 사용자 탈퇴 시 DB에 상태코드 WITHDRAW 로 업데이트
     *
     * @param requestDto     검증 시 필요한 비밀번호가 담긴 DTO
     * @param authentication 사용자 이메일을 가져올 수 있는 인증 객체
     * @throws CustomResponseStatusException 비밀번호가 일치하지 않는 경우 발생하는 예외
     */
    @Transactional
    public void deleteUser(UserDeleteRequestDto requestDto, Authentication authentication)
        throws CustomResponseStatusException {

        String email = authentication.getName();

        User user = findByEmail(email);

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new CustomResponseStatusException(ErrorCode.INVALID_PASSWORD);
        }

        // 리프레시 토큰 삭제
        refreshTokenService.deleteRefreshToken(authentication);

        user.deleteUser();
    }

    /**
     * 사용자 프로필 정보 조회
     *
     * @param authentication 사용자 이메일을 가져올 수 있는 인증 객체
     * @return 프로필 정보가 담긴 DTO
     */
    public UserProfileResponseDto getProfile(Authentication authentication) {
        String email = authentication.getName();

        User user = findByEmail(email);

        return new UserProfileResponseDto(user.getId(), user.getName(), user.getEmail(),
            user.getAddress(), user.getCreatedAt(), user.getUpdatedAt());
    }

    /**
     * 프로필 정보 수정
     *
     * @param requestDto     새롭게 수정할 데이터가 담긴 DTO
     * @param authentication 사용자 이메일을 가져올 수 있는 인증 객체
     * @return 업데이트한 사용자 정보가 담긴 DTO
     * @throws CustomResponseStatusException 비밀번호가 일치하지 않는 경우 발생하는 예외
     */
    @Transactional
    public UserProfileResponseDto updateProfile(UserProfileUpdateRequestDto requestDto,
        Authentication authentication) throws CustomResponseStatusException {

        String email = authentication.getName();

        User user = findByEmail(email);

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new CustomResponseStatusException(ErrorCode.INVALID_PASSWORD);
        }

        Map<String, Object> nonNullFields = requestDto.toNonNullFields();

        user.updateFields(nonNullFields, passwordEncoder);

        return new UserProfileResponseDto(user.getId(), user.getName(), user.getEmail(),
            user.getAddress(), user.getCreatedAt(), user.getUpdatedAt());
    }

    /**
     * 이메일을 통해 사용자 조회하는 메서드 분리
     *
     * @param email 조회할 사용자 이메일
     * @return 조회된 User 객체
     */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("이메일에 해당하는 사용자가 존재하지 않습니다."));
    }

}
