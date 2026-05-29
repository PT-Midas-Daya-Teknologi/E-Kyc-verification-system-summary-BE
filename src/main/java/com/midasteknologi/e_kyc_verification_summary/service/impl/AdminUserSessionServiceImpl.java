package com.midasteknologi.e_kyc_verification_summary.service.impl;

import com.midasteknologi.e_kyc_verification_summary.config.EKycVerificationSummaryConfig;
import com.midasteknologi.e_kyc_verification_summary.entity.AdminUser;
import com.midasteknologi.e_kyc_verification_summary.entity.AdminUserSession;
import com.midasteknologi.e_kyc_verification_summary.repository.AdminUserSessionRepository;
import com.midasteknologi.e_kyc_verification_summary.service.AdminUserSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserSessionServiceImpl implements AdminUserSessionService {

    private final AdminUserSessionRepository adminUserSessionRepository;
    private final EKycVerificationSummaryConfig eKycVerificationSummaryConfig;

    @Override
    public AdminUserSession createSession(AdminUser adminUser) {
        log.info("Inside createSession()");

        AdminUserSession adminUserSession = adminUserSessionRepository.save(
                AdminUserSession
                        .builder()
                        .adminUser(adminUser)
                        .expiry(LocalDateTime.now().plusSeconds(eKycVerificationSummaryConfig.getJwt().getExpiryInSeconds()))
                        .build()
        );

        log.info("Exiting createSession()");
        return adminUserSession;
    }

    @Override
    public AdminUserSession updateSession(AdminUserSession adminUserSession) {
        log.info("Inside updateSession()");

        adminUserSession = adminUserSessionRepository.save(adminUserSession);

        log.info("Exiting updateSession()");
        return adminUserSession;
    }

    @Override
    public void destroySession(UUID sessionId) {
        log.info("Inside destroySession()");

        AdminUserSession adminUserSession = adminUserSessionRepository.findByIdAndIsActiveTrue(sessionId);
        adminUserSession.setIsActive(false);
        adminUserSession.setExpiry(LocalDateTime.now());

        adminUserSessionRepository.save(adminUserSession);

        log.info("Exiting destroySession()");
    }

    @Override
    public boolean validateSession(UUID sessionId) {
        log.info("Inside validateSession()");

        int count = adminUserSessionRepository.countByIdAndExpiryIsGreaterThanAndIsActiveTrue(sessionId, LocalDateTime.now());
        if (count == 1) {
            log.info("session is valid");
            return true;
        }

        log.info("Exiting validateSession()");
        return false;
    }
}
