package com.midasteknologi.e_kyc_verification_summary.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class BeanConfig {

    private final EKycVerificationSummaryConfig eKycVerificationSummaryConfig;

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new DelegatingSecurityContextRepository(
                new RequestAttributeSecurityContextRepository(),
                new HttpSessionSecurityContextRepository()
        );
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(eKycVerificationSummaryConfig.getCorsConfig().getAllowedOrigins())
                        .allowedMethods(eKycVerificationSummaryConfig.getCorsConfig().getAllowedMethods())
                        .allowedHeaders(eKycVerificationSummaryConfig.getCorsConfig().getAllowedHeaders())
                        .allowCredentials(eKycVerificationSummaryConfig.getCorsConfig().getAllowedCredentials())
                        .maxAge(eKycVerificationSummaryConfig.getCorsConfig().getMaxAge());
            }
        };
    }
}
