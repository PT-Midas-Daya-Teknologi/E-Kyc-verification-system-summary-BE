package com.midasteknologi.e_kyc_verification_summary.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "config")
public class EKycVerificationSummaryConfig {

    @Valid
    @NotNull
    private JwtConfig jwt;

    @NotNull
    @NotEmpty
    private List<String> allowedUrls;

    @NotBlank
    private String secretKey;

    @NotBlank
    private String ivKey;

    @Valid
    @NotNull
    private CorsConfig corsConfig;

    @Data
    public static class JwtConfig {

        @NotBlank
        private String secretKey;

        @NotNull
        @PositiveOrZero
        private Long expiryInSeconds;
    }

    @Data
    public static class CorsConfig {

        @NotNull
        private List<String> allowedOrigins;

        @NotNull
        private List<String> allowedMethods;

        @NotNull
        private List<String> allowedHeaders;

        @NotNull
        private Boolean allowedCredentials;

        @NotNull
        @Positive
        private Long maxAge;
    }
}
