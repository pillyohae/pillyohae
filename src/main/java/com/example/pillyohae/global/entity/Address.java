package com.example.pillyohae.global.entity;

import com.example.pillyohae.global.validation.ValidPhoneNumber;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Address {
    @Column(nullable = false)
    private String receiverName;

    @ValidPhoneNumber
    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String zipCode;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String addressDetail;

    protected Address(String receiverName, String phoneNumber, String zipCode,
                      String address, String addressDetail) {
        this.receiverName = receiverName;
        this.phoneNumber = phoneNumber;
        this.zipCode = zipCode;
        this.address = address;
        this.addressDetail = addressDetail;
    }
}