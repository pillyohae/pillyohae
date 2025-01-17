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
    private String receiverName;

    @ValidPhoneNumber
    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String postCode;

    @Column(nullable = false)
    private String roadAddress;

    @Column(nullable = false)
    private String detailAddress;

    protected Address(String receiverName, String phoneNumber, String postCode,
        String roadAddress, String detailAddress) {
        this.receiverName = receiverName;
        this.phoneNumber = phoneNumber;
        this.postCode = postCode;
        this.roadAddress = roadAddress;
        this.detailAddress = detailAddress;
    }
}
