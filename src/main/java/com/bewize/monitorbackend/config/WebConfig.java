package com.bewize.monitorbackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebConfig {

    @Value("${app.cors.allowed-origins:http://localhost:3000,https://support.bewize.ma,http://51.15.132.204:4000}")
    private String allowedOrigins;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                String[] origins = allowedOrigins.split("\\s*,\\s*");
                List<String> patterns = new ArrayList<>();
                List<String> exactOrigins = new ArrayList<>();

                for (String origin : origins) {
                    if (origin != null && !origin.isBlank()) {
                        if (origin.contains("*")) {
                            // Convert wildcard to regex pattern for allowedOriginPatterns
                            patterns.add(origin.replace(".", "\\.").replace("*", ".*"));
                        } else {
                            exactOrigins.add(origin);
                        }
                    }
                }

                var mapping = registry.addMapping("/**")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);

                if (!patterns.isEmpty()) {
                    // Use allowedOriginPatterns for wildcard support
                    String[] allPatterns = new String[patterns.size() + exactOrigins.size()];
                    patterns.toArray(allPatterns);
                    for (int i = 0; i < exactOrigins.size(); i++) {
                        allPatterns[patterns.size() + i] = exactOrigins.get(i);
                    }
                    mapping.allowedOriginPatterns(allPatterns);
                } else if (!exactOrigins.isEmpty()) {
                    mapping.allowedOrigins(exactOrigins.toArray(new String[0]));
                }
            }
        };
    }
}
