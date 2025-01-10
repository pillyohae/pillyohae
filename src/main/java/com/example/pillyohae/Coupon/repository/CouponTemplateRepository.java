package com.example.pillyohae.Coupon.repository;


import com.example.pillyohae.Coupon.entity.CouponTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface CouponTemplateRepository extends JpaRepository<CouponTemplate,Long > {
    int countByExpiredAt(LocalDateTime expiredAt);
    @Transactional(readOnly = true)
    @Query(value = "SELECT id FROM coupon_template " +
            "WHERE expired_at <= :date AND status != :newStatus " +
            "ORDER BY id LIMIT :limit",
            nativeQuery = true)
    List<Long> findExpiredTemplateIds(
            LocalDateTime date,
            String newStatus,
            int limit
    );
    @Transactional
    @Modifying
    @Query(value = "UPDATE coupon_template " +
            "SET status = :newStatus " +
            "WHERE id IN (:ids)",
            nativeQuery = true)
    int updateTemplateStatus(
            List<Long> ids,
            String newStatus
    );
}
