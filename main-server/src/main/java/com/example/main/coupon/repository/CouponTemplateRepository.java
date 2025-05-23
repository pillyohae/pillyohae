package com.example.main.coupon.repository;


import com.example.common.coupon.entity.CouponTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CouponTemplateRepository extends JpaRepository<CouponTemplate,UUID > , CouponTemplateQueryRepository {

    CouponTemplate findByName(String name);
}
