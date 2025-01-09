package com.example.pillyohae.global.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

// application.yml의 security: 아래 설정들을 이 클래스와 매핑
@ConfigurationProperties(prefix = "security")
@Component
@Getter
@Setter
public class SecurityProperties {

    private List<String> whiteList = new ArrayList<>();  // security.white-list와 매핑
    private List<String> sellerAuthList = new ArrayList<>(); // security.seller-auth-list와 매핑

    // security.method-specific-patterns와 매핑
    private Map<HttpMethod, List<String>> methodSpecificPatterns = new HashMap<>();
}
