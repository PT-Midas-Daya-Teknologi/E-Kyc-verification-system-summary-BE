package com.midasteknologi.e_kyc_verification_summary.provider;

import com.midasteknologi.e_kyc_verification_summary.dto.response.LogoutResponse;
import com.midasteknologi.e_kyc_verification_summary.service.AdminUserSessionService;
import com.midasteknologi.e_kyc_verification_summary.service.JwtService;
import com.midasteknologi.e_kyc_verification_summary.util.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.OutputStream;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {

    private final JwtService jwtService;
    private final ResponseUtil respUtil;
    private final ObjectMapper objectMapper;
    private final AdminUserSessionService adminUserSessionService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, @Nullable Authentication authentication) {
        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7);

        UUID sessionId = jwtService.getSessionIdFromToken(token);
        adminUserSessionService.destroySession(sessionId);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.OK.value());

        try {
            OutputStream outputStream = response.getOutputStream();
            objectMapper.writeValue(outputStream, respUtil.buildBody(
                    LogoutResponse
                            .builder()
                            .status(Boolean.TRUE)
                            .build()
            ));
            outputStream.flush();
        } catch (Exception e) {
            log.error("Exception: ", e);
        }
    }
}
