package com.example.pillyohae.user.entity;

import com.example.pillyohae.coupon.entity.IssuedCoupon;
import com.example.pillyohae.order.entity.Order;
import com.example.pillyohae.global.entity.BaseTimeEntity;
import com.example.pillyohae.order.entity.OrderProduct;
import com.example.pillyohae.user.entity.type.Role;
import com.example.pillyohae.user.entity.type.Status;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    private String address;

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

    public void updateFields(Map<String, Object> fields, PasswordEncoder passwordEncoder) {
        fields.forEach((fieldName, value) -> {
            try {
                Field field = User.class.getDeclaredField(fieldName);
                field.setAccessible(true);

                if ("password".equals(fieldName)) {
                    field.set(this, passwordEncoder.encode(value.toString()));
                } else {
                    field.set(this, value);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException("필드 업데이트 실패: " + fieldName, e);
            }
        });
    }

}
