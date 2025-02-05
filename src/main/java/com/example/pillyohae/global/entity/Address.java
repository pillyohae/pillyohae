package com.example.pillyohae.global.entity;

import com.example.pillyohae.global.validation.ValidPhoneNumber;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {

    @Column(nullable = false)
    protected String receiverName;

    @ValidPhoneNumber
    @Column(nullable = false)
    protected String phoneNumber;

    @Column(nullable = false)
    protected String postCode;

    @Column(nullable = false)
    protected String roadAddress;

    @Column(nullable = false)
    protected String detailAddress;

    protected Address(String receiverName, String phoneNumber, String postCode,
        String roadAddress, String detailAddress) {
        this.receiverName = receiverName;
        this.phoneNumber = phoneNumber;
        this.postCode = postCode;
        this.roadAddress = roadAddress;
        this.detailAddress = detailAddress;
    }
}
