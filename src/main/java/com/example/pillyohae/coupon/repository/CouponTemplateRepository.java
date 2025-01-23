package com.example.pillyohae.coupon.repository;


import com.example.pillyohae.coupon.entity.CouponTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CouponTemplateRepository extends JpaRepository<CouponTemplate,UUID > , CouponTemplateQueryRepository{

    CouponTemplate findByName(String name);
}
