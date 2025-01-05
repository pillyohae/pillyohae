package com.example.pillyohae.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {


    private static final String[] AUTH_REQUIRED_PATH_PATTERNS = {"/users/logout", "/workspaces/**"};
    private static final String[] ADMIN_ROLE_REQUIRED_PATH_PATTERNS = {"/workspaces/admin"};



}

