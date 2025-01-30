package com.example.pillyohae.global.config;

import com.example.pillyohae.global.filter.JwtAuthFilter;
import com.example.pillyohae.user.entity.type.Role;
import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration
@EnableWebSecurity // SecurityFilterChain 빈 설정을 위해 필요.
@RequiredArgsConstructor
public class WebConfig {

    private final JwtAuthFilter jwtAuthFilter;

    private final AuthenticationProvider authenticationProvider;

    private final AuthenticationEntryPoint authEntryPoint;

    private final AccessDeniedHandler accessDeniedHandler;

    private final SecurityProperties securityProperties;

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("*"); // 모든 출처 허용
        configuration.addAllowedMethod("*"); // 모든 HTTP 메서드 허용
        configuration.addAllowedHeader("*"); // 모든 헤더 허용
        configuration.addExposedHeader("Authorization"); // 클라이언트가 접근 가능한 헤더
        configuration.addExposedHeader("Set-Cookie");
        configuration.setAllowCredentials(true); // 인증 정보 포함 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(securityProperties.getWhiteList().toArray(new String[0]))
                .permitAll()
                .requestMatchers(HttpMethod.GET, "/products/*").permitAll()
                .requestMatchers(HttpMethod.GET, "/products").permitAll()
                .requestMatchers(HttpMethod.POST, "/persona/*").permitAll()
                .requestMatchers(HttpMethod.POST, "/refresh").permitAll()
                .requestMatchers(HttpMethod.GET, "/coupons/available").permitAll()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.INCLUDE,
                    DispatcherType.ERROR).permitAll()
                .requestMatchers(securityProperties.getSellerAuthList().toArray(new String[0]))
                //스프링 시큐리티의 hasRole 메서드는 내부적으로 ROLE_ 접두사를 해당 값에 자동으로 추가하도록 설계됨.
                //"SELLER" 를 넣어도 ROLE_SELLER 와 비교하게 된다는 뜻.
                .hasRole(Role.SELLER.getName().toUpperCase())
                .requestMatchers(securityProperties.getAdminAuthList().toArray(new String[0]))
                .hasRole(Role.ADMIN.getName().toUpperCase())
                .anyRequest().authenticated()
            )
            // Spring Security 예외에 대한 처리를 핸들러에 위임.
            .exceptionHandling(handler -> handler
                .authenticationEntryPoint(authEntryPoint)
                .accessDeniedHandler(accessDeniedHandler))
            // JWT 기반 테스트를 위해 SecurityContext를 가져올 때 HttpSession을 사용하지 않도록 설정.
            .sessionManagement(
                session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider)

            .addFilterAfter(jwtAuthFilter, ExceptionTranslationFilter.class);

        return http.build();
    }

}

