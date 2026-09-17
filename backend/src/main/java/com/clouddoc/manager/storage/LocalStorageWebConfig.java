package com.clouddoc.manager.storage;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class LocalStorageWebConfig implements WebMvcConfigurer {

    private final LocalDiskStorageService localStorage;
    private final String allowedOrigin;

    public LocalStorageWebConfig(LocalDiskStorageService localStorage,
                                 @Value("${app.cors.allowed-origin:http://localhost:5500}") String allowedOrigin) {
        this.localStorage = localStorage;
        this.allowedOrigin = allowedOrigin;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(localStorage.rootDirectory().toUri().toString());
    }

    @Override
    public void addCorsMappings(org.springframework.web.servlet.config.annotation.CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigin)
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowedHeaders("*");
    }
}