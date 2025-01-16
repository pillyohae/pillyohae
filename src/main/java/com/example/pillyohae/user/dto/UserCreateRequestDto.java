package com.example.pillyohae.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserCreateRequestDto {

    @Size(min = 2, max = 20, message = "이름은 2자 이상 20자 이하여야 합니다.")
    @NotBlank(message = "이름을 입력해주세요.")
    private final String name;

    @NotBlank(message = "이메일을 입력해주세요")
    private final String email;

    private final String phoneNumber;

    private final String postcode;

    private final String roadAddress;

    private final String detailAddress;

    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
        message = "비밀번호는 최소 8자 이상이어야 하며, 대소문자, 숫자, 특수문자를 포함해야 합니다"
    )
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하여야 합니다")
    @NotBlank(message = "패스워드를 입력해주세요")
    private final String password;

    @NotBlank(message = "권한을 선택해주세요")
    private final String role;
}
