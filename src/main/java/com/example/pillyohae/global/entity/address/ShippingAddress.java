package com.example.pillyohae.global.entity.address;

import com.example.pillyohae.global.entity.Address;
import com.example.pillyohae.user.dto.UserProfileUpdateRequestDto;
import jakarta.persistence.Embeddable;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShippingAddress extends Address {

    public ShippingAddress(String receiverName, String phoneNumber, String postCode,
        String roadAddress, String detailAddress) {
        super(receiverName, phoneNumber, postCode, roadAddress, detailAddress);
    }

    public void update(UserProfileUpdateRequestDto.AddressUpdateDto dto) {
        if (dto == null) {
            return;
        }

        Optional.ofNullable(dto.getReceiverName())
            .ifPresent(name -> this.receiverName = name);
        Optional.ofNullable(dto.getPhoneNumber())
            .ifPresent(phone -> this.phoneNumber = phone);
        Optional.ofNullable(dto.getPostCode())
            .ifPresent(code -> this.postCode = code);
        Optional.ofNullable(dto.getRoadAddress())
            .ifPresent(road -> this.roadAddress = road);
        Optional.ofNullable(dto.getDetailAddress())
            .ifPresent(detail -> this.detailAddress = detail);
    }
}
