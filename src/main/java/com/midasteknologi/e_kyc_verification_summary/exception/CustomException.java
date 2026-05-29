package com.midasteknologi.e_kyc_verification_summary.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CustomException extends Exception {

    private String code;

    private String message;

    private String type;
}
