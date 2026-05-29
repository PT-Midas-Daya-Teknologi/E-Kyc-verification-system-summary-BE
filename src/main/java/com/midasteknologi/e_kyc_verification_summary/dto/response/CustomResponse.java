package com.midasteknologi.e_kyc_verification_summary.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomResponse<T> {

    private Boolean success;

    private LocalDateTime responseTime;

    private Object errors;

    private T body;
}
