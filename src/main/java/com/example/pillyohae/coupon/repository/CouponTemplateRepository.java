package com.example.pillyohae.coupon.repository;


import com.example.pillyohae.coupon.entity.CouponTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface CouponTemplateRepository extends JpaRepository<CouponTemplate,Long > {
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

    @Transactional(readOnly = true)
    @Query(value = "SELECT id FROM coupon_template " +
            "WHERE expired_at <= :date AND :startAt<= expired_at AND status != :newStatus " +
            "ORDER BY id LIMIT :limit",
            nativeQuery = true)
    List<Long> findExpiredTemplateIdsBetweenTimes(
            LocalDateTime startAt,
            LocalDateTime endAt,
            String newStatus,
            int limit
    );


    @Transactional(readOnly = true)
    @Query(value = "SELECT id FROM coupon_template " +
            "WHERE startAt <= :date AND status = : nowState ",
            nativeQuery = true)
    List<Long> findTemplateIdsByStartAtAndNowState(
            LocalDateTime date,
            String nowState
    );


    int countByExpiredAtBetween(LocalDateTime expiredAtAfter, LocalDateTime expiredAtBefore);

    int countByExpiredAt(LocalDateTime expiredAt);
}