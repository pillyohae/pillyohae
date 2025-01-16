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

    @NotBlank(message = "전화번호를 입력해주세요")
    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "올바른 전화번호 형식이 아닙니다")
    private final String phoneNumber;

    @NotBlank(message = "우편번호를 입력해주세요")
    @Pattern(regexp = "^\\d{5}$", message = "올바른 우편번호 형식이 아닙니다")
    private final String postcode;

    @NotBlank(message = "도로명 주소를 입력해주세요")
    private final String roadAddress;

    @NotBlank(message = "상세 주소를 입력해주세요")
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
