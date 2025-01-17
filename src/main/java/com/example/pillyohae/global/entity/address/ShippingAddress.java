package com.example.pillyohae.global.entity.address;

import com.example.pillyohae.global.entity.Address;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShippingAddress extends Address {

    public ShippingAddress(String receiverName, String phoneNumber, String postCode,
        String roadAddress, String detailAddress) {
        super(receiverName, phoneNumber, postCode, roadAddress, detailAddress);
    }
}
