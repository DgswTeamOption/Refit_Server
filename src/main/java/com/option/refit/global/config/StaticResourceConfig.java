package com.option.refit.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

// 업로드된 이미지를 /uploads/** 로 제공
@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    private final Path uploadDirectory;

    public StaticResourceConfig(@Value("${file.upload-dir:${user.home}/uploads}") String uploadDir) {
        this.uploadDirectory = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 폴더가 아직 없으면 toUri()에 끝 슬래시가 붙지 않으므로 직접 보장
        String location = uploadDirectory.toUri().toString();
        if (!location.endsWith("/")) {
            location += "/";
        }
        // 파일명이 UUID라 내용이 바뀌지 않으므로 클라이언트가 재다운로드하지 않도록 장기 캐시
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location)
                .setCacheControl(CacheControl.maxAge(Duration.ofDays(30)).cachePublic().immutable());
    }
}
