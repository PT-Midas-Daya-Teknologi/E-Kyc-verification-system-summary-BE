package com.midasteknologi.e_kyc_verification_summary.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomError {

    private String code;

    private String type;

    private String message;
}
