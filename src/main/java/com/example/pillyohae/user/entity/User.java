package com.example.pillyohae.user.entity;

import com.example.pillyohae.order.entity.Order;
import com.example.pillyohae.order.entity.OrderItem;
import com.example.pillyohae.global.entity.BaseTimeEntity;
import com.example.pillyohae.user.entity.type.Role;
import com.example.pillyohae.user.entity.type.Status;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    private String password;

    private String address;

    @Enumerated(value = EnumType.STRING)
    private Role role;

    @Enumerated(value = EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "seller", fetch = FetchType.LAZY)
    private List<OrderItem> sellerOrders = new ArrayList<>();

    public User(String name, String email, String password, String address, Role role) {
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

    public void updateName(String newName) {
        this.name = newName;
    }
    public void updateEmail(String newEmail) {
        this.email = newEmail;
    }

    public void updateAddress(String newAddress) {
        this.address = newAddress;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }
}
