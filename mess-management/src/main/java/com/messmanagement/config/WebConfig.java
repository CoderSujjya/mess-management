package com.messmanagement.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/**
 * Exposes the uploads folder (where the mess logo lives) at /uploads/**
 * so it can be shown directly in an <img> tag, e.g. /uploads/logo.png.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final String uploadDirProperty;

    public WebConfig(@Value("${app.upload.dir:uploads}") String uploadDirProperty) {
        this.uploadDirProperty = uploadDirProperty;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String absolutePath = Paths.get(uploadDirProperty).toAbsolutePath().normalize().toString();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + absolutePath + "/");
    }
}
