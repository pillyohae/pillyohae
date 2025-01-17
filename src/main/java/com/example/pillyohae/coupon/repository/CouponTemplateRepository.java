package com.example.pillyohae.coupon.repository;


import com.example.pillyohae.coupon.dto.FindCouponListResponseDto;
import com.example.pillyohae.coupon.entity.CouponTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface CouponTemplateRepository extends JpaRepository<CouponTemplate,Long > , CouponTemplateQueryRepository{

    CouponTemplate findByName(String name);
}
