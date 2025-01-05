package com.example.pillyohae.user.service;

import com.example.pillyohae.global.util.JwtProvider;
import com.example.pillyohae.user.dto.UserCreateRequestDto;
import com.example.pillyohae.user.dto.UserCreateResponseDto;
import com.example.pillyohae.user.dto.UserLoginRequestDto;
import com.example.pillyohae.user.entity.User;
import com.example.pillyohae.user.entity.type.Role;
import com.example.pillyohae.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

        User user = new User(
            requestDto.getName(), requestDto.getEmail(),
            passwordEncoder.encode(requestDto.getPassword()),
            requestDto.getAddress(),
            Role.of(requestDto.getRole())
        );

        User savedUser = userRepository.save(user);

        return new UserCreateResponseDto(
            savedUser.getId(),
            savedUser.getName(),
            savedUser.getEmail(),
            savedUser.getAddress(),
            savedUser.getCreatedAt()
        );
    }

    public String loginTokenGenerate(UserLoginRequestDto requestDto) {

        // 이 과정에서 Provider 가 인증 처리를 진행 (사용자 정보 조회, 비밀번호 검증)
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                requestDto.getEmail(),
                requestDto.getPassword()
            )
        );

        // 인증 객체를 SecurityContext에 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // JWT 생성 후 반환
        return jwtProvider.generateToken(authentication);
    }

}
