package com.example.pillyohae.Coupon.repository;

import com.example.pillyohae.Coupon.entity.CouponTemplate;
import com.example.pillyohae.Coupon.entity.IssuedCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface IssuedCouponRepository extends JpaRepository<IssuedCoupon, Long> {
    // native query mysql batch 처리
    @Transactional
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
    @Transactional
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

    @Query("SELECT ic FROM IssuedCoupon ic " +
            "JOIN FETCH ic.couponTemplate " +
            "WHERE ic.user.id = :userId")
    List<IssuedCoupon> findIssuedCouponsWithTemplateByUserId(@Param("userId") Long userId);

    int countIssuedCouponByCouponTemplate_Id(Long couponTemplateId);
}
