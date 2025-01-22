package com.example.pillyohae.user.entity;

import com.example.pillyohae.coupon.entity.IssuedCoupon;
import com.example.pillyohae.global.entity.BaseTimeEntity;
import com.example.pillyohae.global.entity.address.ShippingAddress;
import com.example.pillyohae.global.exception.CustomResponseStatusException;
import com.example.pillyohae.global.exception.code.ErrorCode;
import com.example.pillyohae.order.entity.Order;
import com.example.pillyohae.order.entity.OrderProduct;
import com.example.pillyohae.user.dto.UserProfileUpdateRequestDto;
import com.example.pillyohae.user.entity.type.Role;
import com.example.pillyohae.user.entity.type.Status;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Entity
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    private String password;

    @Embedded
    private ShippingAddress address;

    @Enumerated(value = EnumType.STRING)
    private Role role;

    @Enumerated(value = EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "seller", fetch = FetchType.LAZY)
    private List<OrderProduct> sellerOrders = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<IssuedCoupon> issuedCoupons = new ArrayList<>();

    public User(String name, String email, String password, ShippingAddress address, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.address = address;
        this.role = role;
        this.status = Status.ACTIVE;
    }

    public User() {

    }


    public void deleteUser() {
        this.status = Status.WITHDRAW;
    }

    public void updateProfile(UserProfileUpdateRequestDto dto, PasswordEncoder passwordEncoder) {
        // 비밀번호 검증
        if (!passwordEncoder.matches(dto.getPassword(), this.password)) {
            throw new CustomResponseStatusException(ErrorCode.INVALID_PASSWORD);
        }

        // 선택적 필드 업데이트
        Optional.ofNullable(dto.getNewName())
            .ifPresent(name -> this.name = name);

        Optional.ofNullable(dto.getNewPassword())
            .ifPresent(newPassword -> this.password = passwordEncoder.encode(newPassword));

        Optional.ofNullable(dto.getNewAddress())
            .ifPresent(this::updateAddress);
    }

    private void updateAddress(UserProfileUpdateRequestDto.AddressUpdateDto addressDto) {

        this.address.update(addressDto);
    }

}
