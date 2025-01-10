package com.example.pillyohae.Coupon.repository;

import com.example.pillyohae.Coupon.entity.IssuedCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface IssuedCouponRepository extends JpaRepository<IssuedCoupon, Long> {
    // native query mysql batch 처리
    @Modifying
    @Query(value = "UPDATE issued_coupon " +
            "SET status = :status " +
            "WHERE expired_at <= :date AND status == :status " +
            "ORDER BY id LIMIT :limit",
            nativeQuery = true)
    int updateStatusByExpiredAtWithLimit(
            LocalDateTime date,
            String status,  // Enum을 String으로 받아야 함
            int limit
    );
}
