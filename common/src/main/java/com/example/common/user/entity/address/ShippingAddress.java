package com.example.common.user.entity.address;

import com.example.common.global.entity.Address;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShippingAddress extends Address {

    public ShippingAddress(String receiverName, String phoneNumber, String postCode,
        String roadAddress, String detailAddress) {
        super(receiverName, phoneNumber, postCode, roadAddress, detailAddress);
    }


    public void update(String receiverName, String phoneNumber, String postCode, String roadAddress, String detailAddress) {

        Optional.ofNullable(receiverName)
            .ifPresent(name -> this.receiverName = name);
        Optional.ofNullable(phoneNumber)
            .ifPresent(phone -> this.phoneNumber = phone);
        Optional.ofNullable(postCode)
            .ifPresent(code -> this.postCode = code);
        Optional.ofNullable(roadAddress)
            .ifPresent(road -> this.roadAddress = road);
        Optional.ofNullable(detailAddress)
            .ifPresent(detail -> this.detailAddress = detail);
    }
}
