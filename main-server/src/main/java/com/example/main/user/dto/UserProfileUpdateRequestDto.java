package com.example.main.user.dto;

import com.example.common.validation.ValidPhoneNumber;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileUpdateRequestDto {

    @NotBlank(message = "기존 비밀번호는 필수 입력 값입니다.")
    private String password;  // 현재 비밀번호 (필수값)

    @Size(min = 2, max = 20, message = "이름은 2자 이상 20자 이하여야 합니다.")
    private String newName;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
        message = "비밀번호는 최소 8자 이상이어야 하며, 대소문자, 숫자, 특수문자를 포함해야 합니다")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하여야 합니다")
    private String newPassword;

    @Valid
    private AddressUpdateDto newAddress;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressUpdateDto {

        private String receiverName;

        @ValidPhoneNumber
        private String phoneNumber;

        private String postCode;

        private String roadAddress;

        private String detailAddress;
    }

}
