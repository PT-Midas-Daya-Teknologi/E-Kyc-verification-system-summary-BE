package com.midasteknologi.e_kyc_verification_summary.service;

import com.midasteknologi.e_kyc_verification_summary.entity.AdminUser;
import com.midasteknologi.e_kyc_verification_summary.entity.AdminUserSession;

import java.util.UUID;

public interface AdminUserSessionService {

    AdminUserSession createSession(AdminUser adminUser);

    AdminUserSession updateSession(AdminUserSession adminUserSession);

    void destroySession(UUID sessionId) throws Exception;

    boolean validateSession(UUID sessionId);
}
