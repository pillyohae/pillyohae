package com.example.pillyohae.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileUpdateRequestDto {
    private String newName;
    private String newAddress;
    private String newPassword;

    @NotBlank(message = "기존 비밀번호는 필수 입력 값입니다.")
    private String password;
}
