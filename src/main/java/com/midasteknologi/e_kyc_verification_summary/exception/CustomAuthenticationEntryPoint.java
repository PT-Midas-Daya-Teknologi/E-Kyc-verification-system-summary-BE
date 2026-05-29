package com.midasteknologi.e_kyc_verification_summary.exception;

import com.midasteknologi.e_kyc_verification_summary.constants.GlobalConstant;
import com.midasteknologi.e_kyc_verification_summary.dto.response.CustomError;
import com.midasteknologi.e_kyc_verification_summary.util.ResponseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.OutputStream;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ResponseUtil respUtil;
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        CustomError customError = CustomError
                .builder()
                .code(GlobalConstant.UNAUTHORIZE_ERROR_CODE)
                .message(GlobalConstant.UNAUTHORIZE_ERROR_MESSAGE)
                .type(GlobalConstant.UNAUTHORIZE_ERROR_TYPE)
                .build();

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        OutputStream outputStream = response.getOutputStream();
        objectMapper.writeValue(outputStream, respUtil.buildErrors(customError));
        outputStream.flush();
    }
}
