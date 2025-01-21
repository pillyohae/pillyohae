package com.example.pillyohae.coupon.service;

import com.example.pillyohae.coupon.entity.CouponTemplate;
import com.example.pillyohae.coupon.repository.CouponTemplateRepository;
import com.example.pillyohae.coupon.repository.IssuedCouponRepository;
import com.example.pillyohae.global.config.RedisConfig;
import com.example.pillyohae.global.entity.address.ShippingAddress;
import com.example.pillyohae.refresh.service.RefreshTokenService;
import com.example.pillyohae.user.entity.User;
import com.example.pillyohae.user.entity.type.Role;
import com.example.pillyohae.user.repository.UserRepository;
import groovy.util.logging.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.DURATION;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CouponServiceTest {
    @Autowired
    private CouponService couponService;
    @Autowired
    private CouponTemplateRepository couponTemplateRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private IssuedCouponRepository issuedCouponRepository;
    @Autowired
    private RedisTemplate<String,Integer> intRedisTemplate;

    private List<User> users = new ArrayList<>();
    private CouponTemplate couponTemplate;

    @BeforeEach
    void setUp() {
        couponTemplate = new CouponTemplate("couponTemplate1", "description", CouponTemplate.DiscountType.FIXED_AMOUNT, CouponTemplate.ExpiredType.FIXED_DATE,10000L,null,10000L,20000L, LocalDateTime.now().plusSeconds(1L),LocalDateTime.now().plusDays(1L),100,0);
        couponTemplateRepository.save(couponTemplate);
        ShippingAddress address = new ShippingAddress("TestUser","010-0000-0000","test-zip","test-road","100-100");
        String userName = "user";
        String email = "user@example.com";
        String password = "Asdf1234@";
        Role role = Role.BUYER;
        for (int i = 0; i < 1000; i++) {
            User user = new User(userName+i,email+i,password,address,role);
            users.add(user);
        }
        userRepository.saveAll(users);
    }

    @Test
    @DisplayName("쿠폰 발급 동시성 테스트")
    void giveCouponSynchronicityTest() throws InterruptedException {

        Thread.sleep(4000);
        LocalDateTime start = LocalDateTime.now();
        try {
            IntStream.range(1, 1000).parallel().forEach(i -> couponService.giveCoupon("user@example.com" + i , couponTemplate.getId()));
        }catch (Exception e){
        }
        LocalDateTime end = LocalDateTime.now();

        System.out.println("소요 시간: " + Duration.between(start, end).toMillis() + "ms");

        Thread.sleep(2000);
        CouponTemplate couponTemplate =  couponTemplateRepository.findByName("couponTemplate1");

        String countKey = "coupon:count:" + couponTemplate.getId();

        // 1. 캐시로부터 쿠폰 갯수를 가져옴
        Integer count = intRedisTemplate.opsForValue().get(countKey);
        // redis에 기록된 쿠폰 수 와 최대 발행량 일치  확인
        assertThat(count).isEqualTo(couponTemplate.getMaxIssuanceCount());
        // 발행된 쿠폰수와 최대 발행량이 일치하는지 확인
        assertThat(couponTemplate.getIssuedCoupons().size()).isEqualTo(couponTemplate.getMaxIssuanceCount());

    }
}