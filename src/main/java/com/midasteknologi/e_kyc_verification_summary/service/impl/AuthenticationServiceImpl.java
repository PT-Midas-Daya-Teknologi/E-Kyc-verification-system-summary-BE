package com.midasteknologi.e_kyc_verification_summary.service.impl;

import com.midasteknologi.e_kyc_verification_summary.constants.GlobalConstant;
import com.midasteknologi.e_kyc_verification_summary.dto.request.AuthenticationRequest;
import com.midasteknologi.e_kyc_verification_summary.dto.response.AuthenticationResponse;
import com.midasteknologi.e_kyc_verification_summary.entity.AdminUser;
import com.midasteknologi.e_kyc_verification_summary.entity.AdminUserSession;
import com.midasteknologi.e_kyc_verification_summary.exception.CustomException;
import com.midasteknologi.e_kyc_verification_summary.repository.AdminUserRepository;
import com.midasteknologi.e_kyc_verification_summary.service.AdminUserSessionService;
import com.midasteknologi.e_kyc_verification_summary.service.AuthenticationService;
import com.midasteknologi.e_kyc_verification_summary.service.JwtService;
import com.midasteknologi.e_kyc_verification_summary.util.SystemContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final JwtService jwtService;
    private final AdminUserRepository adminUserRepository;
    private final AuthenticationManager authenticationManager;
    private final AdminUserSessionService adminUserSessionService;
    private final SecurityContextRepository securityContextRepository;

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws CustomException {
        log.info("Inside authenticate()");
        AuthenticationResponse authenticationResponse = null;

        try {
            AdminUser adminUser = adminUserRepository.findByEmail(authenticationRequest.getUsername())
                    .orElseThrow(() -> new CustomException(
                            GlobalConstant.NOT_FOUND_ERROR_CODE,
                            GlobalConstant.NOT_FOUND_ERROR_MESSAGE,
                            GlobalConstant.NOT_FOUND_ERROR_TYPE
                    ));

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(adminUser.getEmail(), authenticationRequest.getPassword())
            );

            if (authentication.isAuthenticated()) {
                AdminUserSession adminUserSession = adminUserSessionService.createSession(adminUser);
                String token = jwtService.generateAccessToken(adminUser, adminUserSession.getId());
                adminUserSession.setAccessToken(token);
                adminUserSession = adminUserSessionService.updateSession(adminUserSession);

                SystemContext.setContext(GlobalConstant.SESSION_ID, adminUserSession.getId());
                SecurityContext securityContext = SecurityContextHolder.getContext();
                securityContext.setAuthentication(authentication);
                securityContextRepository.saveContext(securityContext, httpServletRequest, httpServletResponse);

                authenticationResponse = AuthenticationResponse
                        .builder()
                        .accessToken(token)
                        .build();
            } else {
                throw new UsernameNotFoundException("Invalid credentials");
            }
        } catch (CustomException | BadCredentialsException | UsernameNotFoundException e) {
            log.error("Exception: ", e);
            throw e;
        } catch (Exception e) {
            log.error("Exception: ", e);
            throw new CustomException(
                    GlobalConstant.INTERNAL_SERVER_ERROR_CODE,
                    GlobalConstant.INTERNAL_SERVER_ERROR_MESSAGE,
                    GlobalConstant.INTERNAL_SERVER_ERROR_TYPE
            );
        }

        log.info("Exiting authenticate()");
        return authenticationResponse;
    }
}
