package com.example.pillyohae.user.service;

import com.example.pillyohae.user.dto.UserCreateRequestDto;
import com.example.pillyohae.user.dto.UserCreateResponseDto;
import com.example.pillyohae.user.entity.User;
import com.example.pillyohae.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserCreateResponseDto createUser(UserCreateRequestDto requestDto)
        throws DuplicateKeyException {

        boolean duplicated = userRepository.findByEmail(requestDto.getEmail()).isPresent();

        if (duplicated) {
            throw new DuplicateKeyException("중복된 이메일 입니다.");
        }

        User user = new User(
            requestDto.getName(), requestDto.getEmail(),
            passwordEncoder.encode(requestDto.getPassword()),
            requestDto.getAddress(), requestDto.getRole()
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
}
