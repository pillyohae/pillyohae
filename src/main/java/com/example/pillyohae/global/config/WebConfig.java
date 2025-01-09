package com.example.pillyohae.global.config;

import com.example.pillyohae.global.filter.JwtAuthFilter;
import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.ExceptionTranslationFilter;


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
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(securityProperties.getWhiteList().toArray(new String[0]))
                .permitAll()
                .requestMatchers(HttpMethod.GET, "/products/*").permitAll()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.INCLUDE,
                    DispatcherType.ERROR).permitAll()
                .requestMatchers(securityProperties.getSellerAuthList().toArray(new String[0]))
                .hasRole("SELLER")
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

