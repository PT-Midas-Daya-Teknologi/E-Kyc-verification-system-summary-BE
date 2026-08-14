package com.midasteknologi.e_kyc_verification_summary.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AudioRequest {

    @NotBlank(message = "Video ID cannot be blank")
    private String videoId;
}
