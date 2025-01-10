package com.example.pillyohae.Coupon.repository;


import com.example.pillyohae.Coupon.entity.CouponTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CouponTemplateRepository extends JpaRepository<CouponTemplate,Long > {
    List<CouponTemplate> findAllByExpiredAtBefore(LocalDateTime expiredAtBefore);
    @Modifying
    @Query("UPDATE CouponTemplate c SET c.status = :status WHERE c.expiredAt <= :date")
    int updateStatusByExpiredAt(LocalDateTime date, CouponTemplate.CouponStatus status);

    @Modifying
    @Query(value = "UPDATE coupon_template " +
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
