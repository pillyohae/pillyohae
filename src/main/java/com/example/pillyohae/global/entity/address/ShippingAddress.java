package com.example.pillyohae.global.entity.address;

import com.example.pillyohae.global.entity.Address;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShippingAddress extends Address {
    public ShippingAddress(String receiverName, String phoneNumber, String zipCode,
                           String address, String addressDetail) {
        super(receiverName, phoneNumber, zipCode, address, addressDetail);
    }
}