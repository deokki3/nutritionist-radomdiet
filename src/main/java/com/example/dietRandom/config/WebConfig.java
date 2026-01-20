package com.example.dietRandom.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // "이것은 설정 파일입니다"
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 1. 모든 주소에 대해서
                .allowedOrigins("http://localhost:5173") // 2. 프론트엔드 주소 허용
                .allowedMethods("GET", "POST", "PUT", "DELETE") // 3. 허용할 HTTP 메소드
                .allowCredentials(true); // 4. 쿠키 인증 요청 허용
    }
}