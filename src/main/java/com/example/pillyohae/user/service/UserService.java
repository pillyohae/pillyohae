package com.example.pillyohae.user.service;

import com.example.pillyohae.global.exception.CustomResponseStatusException;
import com.example.pillyohae.global.exception.code.ErrorCode;
import com.example.pillyohae.global.util.JwtProvider;
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
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

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

    public String loginTokenGenerate(UserLoginRequestDto requestDto) {

        User user = findByEmail(requestDto.getEmail());

        if (user.getStatus() == Status.WITHDRAW) {
            throw new CustomResponseStatusException(ErrorCode.FORBIDDEN_DELETED_USER_LOGIN);
        }

        // 이 과정에서 Provider 가 인증 처리를 진행 (사용자 정보 조회, 비밀번호 검증)
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(requestDto.getEmail(),
                requestDto.getPassword()));

        // 인증 객체를 SecurityContext에 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // JWT 생성 후 반환
        return jwtProvider.generateToken(authentication);
    }

    @Transactional
    public void deleteUser(UserDeleteRequestDto requestDto, Authentication authentication) {

        String email = authentication.getName();

        User user = findByEmail(email);

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "비밀번호가 일치하지 않습니다");
        }

        user.deleteUser();
    }

    public UserProfileResponseDto getProfile(Authentication authentication) {
        String email = authentication.getName();

        User user = findByEmail(email);

        return new UserProfileResponseDto(user.getId(), user.getName(), user.getEmail(),
            user.getAddress(), user.getCreatedAt(), user.getUpdatedAt());
    }

    @Transactional
    public UserProfileResponseDto updateProfile(UserProfileUpdateRequestDto requestDto,
        Authentication authentication) {

        String email = authentication.getName();

        User user = findByEmail(email);

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "비밀번호가 일치하지 않습니다");
        }

        Map<String, Object> nonNullFields = requestDto.toNonNullFields();

        user.updateFields(nonNullFields, passwordEncoder);

        return new UserProfileResponseDto(user.getId(), user.getName(), user.getEmail(),
            user.getAddress(), user.getCreatedAt(), user.getUpdatedAt());
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("이메일에 해당하는 사용자가 존재하지 않습니다."));
    }


}
