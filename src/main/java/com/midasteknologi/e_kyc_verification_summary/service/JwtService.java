package com.midasteknologi.e_kyc_verification_summary.service;

import com.midasteknologi.e_kyc_verification_summary.entity.AdminUser;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

public interface JwtService {

    String generateAccessToken(AdminUser user, UUID sessionId);

    Long getUserIdFromToken(String token);

    String getUserNameFromToken(String token);

    UUID getSessionIdFromToken(String authToken);

    boolean validateToken(String authToken, UserDetails userDetails);
}
