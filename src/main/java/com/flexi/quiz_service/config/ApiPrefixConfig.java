package com.flexi.quiz_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApiPrefixConfig implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // Adds /api/v1 to every controller in this package
        configurer.addPathPrefix("/api/v1", c-> c.getPackageName().startsWith("com.flexi.quiz_service.controller"));
    }
}