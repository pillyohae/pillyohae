package com.example.pillyohae.Coupon.repository;

import com.example.pillyohae.Coupon.entity.CouponTemplate;
import com.example.pillyohae.Coupon.entity.IssuedCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface IssuedCouponRepository extends JpaRepository<IssuedCoupon, Long> {
    // native query mysql batch 처리
    @Modifying
    @Query(value = "UPDATE issued_coupon " +
            "SET status = :newStatus " +
            "WHERE coupon_template_id IN (:templateIds) " +
            "AND status != :newStatus " +
            "AND status = :status2 " +
            "ORDER BY id LIMIT :limit",
            nativeQuery = true)
    int updateIssuedCouponStatusByCouponTemplate_Id_In(
                    List<Long> templateIds,
                    String newStatus,
                    String targetStatus,
                    int limit
            );
    int countIssuedCouponByCouponTemplate_IdIn(Collection<Long> couponTemplateIds);

    @Modifying
    @Query(value = "UPDATE issued_coupon " +
            "SET status = :newStatus " +
            "WHERE coupon_template_id = :couponTemplateId " +
            "AND status != :newStatus " +
            "AND status = :status2 " +
            "ORDER BY id LIMIT :limit",
            nativeQuery = true)
    int updateIssuedCouponStatusByCouponTemplate_Id(
            Long couponTemplateId,
            String newStatus,
            String targetStatus,
            int limit
    );

    int countIssuedCouponByCouponTemplate_Id(Long couponTemplateId);
}
