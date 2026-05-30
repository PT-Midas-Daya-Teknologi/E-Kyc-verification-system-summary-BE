package com.midasteknologi.e_kyc_verification_summary.util;

import com.midasteknologi.e_kyc_verification_summary.dto.response.BaseResponse;
import com.midasteknologi.e_kyc_verification_summary.dto.response.CustomResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ResponseUtil {

    public CustomResponse buildBody(BaseResponse body) {
        return CustomResponse
                .builder()
                .success(true)
                .responseTime(LocalDateTime.now())
                .errors(null)
                .body(body)
                .build();
    }

    public CustomResponse buildErrors(Object errors) {
        return CustomResponse
                .builder()
                .success(false)
                .responseTime(LocalDateTime.now())
                .errors(errors)
                .build();
    }
}
