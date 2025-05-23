package com.example.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@EnableCaching
@SpringBootApplication(scanBasePackages = {"com.example.common", "com.example.main"})
@EntityScan(basePackages = {"com.example.common"})
public class PillyohaeApplication {

    public static void main(String[] args) {
        SpringApplication.run(PillyohaeApplication.class, args);
    }

}
