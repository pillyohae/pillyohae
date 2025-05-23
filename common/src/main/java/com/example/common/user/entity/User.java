package com.example.common.user.entity;


import com.example.common.coupon.entity.IssuedCoupon;
import com.example.common.global.entity.BaseTimeEntity;
import com.example.common.order.entity.Order;
import com.example.common.order.entity.OrderProduct;
import com.example.common.user.entity.address.ShippingAddress;
import com.example.common.user.entity.type.Role;
import com.example.common.user.entity.type.Status;
import jakarta.persistence.*;
import lombok.Generated;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;
    private String name;
    private String email;
    private String password;
    @Embedded
    private ShippingAddress address;
    @Enumerated(EnumType.STRING)
    private Role role;
    @Enumerated(EnumType.STRING)
    private Status status;
    @OneToMany(
            mappedBy = "user",
            fetch = FetchType.LAZY
    )
    private List<Order> orders = new ArrayList();
    @OneToMany(
            mappedBy = "seller",
            fetch = FetchType.LAZY
    )
    private List<OrderProduct> sellerOrders = new ArrayList();
    @OneToMany(
            mappedBy = "user",
            fetch = FetchType.LAZY
    )
    private List<IssuedCoupon> issuedCoupons = new ArrayList();

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

    public void updateProfile(String newName, String newEncodedPassword) {
            Optional.ofNullable(newName).ifPresent((name) -> this.name = name);
            Optional.ofNullable(newEncodedPassword).ifPresent((newPassword) -> this.password =newPassword);
    }


    @Generated
    public Long getId() {
        return this.id;
    }

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public String getEmail() {
        return this.email;
    }

    @Generated
    public String getPassword() {
        return this.password;
    }

    @Generated
    public ShippingAddress getAddress() {
        return this.address;
    }

    @Generated
    public Role getRole() {
        return this.role;
    }

    @Generated
    public Status getStatus() {
        return this.status;
    }

    @Generated
    public List<Order> getOrders() {
        return this.orders;
    }

    @Generated
    public List<OrderProduct> getSellerOrders() {
        return this.sellerOrders;
    }

    @Generated
    public List<IssuedCoupon> getIssuedCoupons() {
        return this.issuedCoupons;
    }
}
