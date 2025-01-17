package com.example.pillyohae.coupon.repository;


import com.example.pillyohae.coupon.entity.CouponTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponTemplateRepository extends JpaRepository<CouponTemplate,Long > , CouponTemplateQueryRepository{

    CouponTemplate findByName(String name);
}
