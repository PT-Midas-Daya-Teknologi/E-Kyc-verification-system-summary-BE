package com.midasteknologi.e_kyc_verification_summary.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "config.python")
public class PythonServiceConfig {

    @NotBlank
    private String VideoDownloadUrl;
}
