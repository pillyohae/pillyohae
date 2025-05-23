package com.example.main.coupon.repository;

import com.example.common.coupon.entity.IssuedCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;


public interface IssuedCouponRepository extends JpaRepository<IssuedCoupon, UUID>, IssuedCouponQueryRepository {
    @Query("SELECT ic FROM IssuedCoupon ic " +
            "JOIN FETCH ic.couponTemplate " +
            "WHERE ic.user.id = :userId")
    List<IssuedCoupon> findIssuedCouponsWithTemplateByUserId(@Param("userId") Long userId);

}
