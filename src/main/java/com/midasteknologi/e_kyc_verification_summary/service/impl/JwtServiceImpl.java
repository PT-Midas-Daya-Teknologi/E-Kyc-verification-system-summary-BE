package com.midasteknologi.e_kyc_verification_summary.service.impl;

import com.midasteknologi.e_kyc_verification_summary.config.EKycVerificationSummaryConfig;
import com.midasteknologi.e_kyc_verification_summary.entity.AdminUser;
import com.midasteknologi.e_kyc_verification_summary.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final EKycVerificationSummaryConfig eKycVerificationSummaryConfig;

    @Override
    public String generateAccessToken(AdminUser user, UUID sessionId) {
        LocalDateTime issuedAt = LocalDateTime.now();
        return Jwts
                .builder()
                .subject(String.valueOf(user.getId()))
                .claims(setClaims(user, sessionId))
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusSeconds(eKycVerificationSummaryConfig.getJwt().getExpiryInSeconds())))
                .signWith(getSecretKey())
                .compact();
    }

    @Override
    public Long getUserIdFromToken(String token) {
        return Long.valueOf(getClaims(token)
                .getSubject());
    }

    @Override
    public String getUserNameFromToken(String token) {
        return String.valueOf(getClaims(token)
                .get("username"));
    }

    @Override
    public UUID getSessionIdFromToken(String authToken) {
        return UUID.fromString((String) getClaims(authToken)
                .get("session_id"));
    }

    public Date getExpirationFromToken(String token) {
        return getClaims(token)
                .getExpiration();
    }

    @Override
    public boolean validateToken(String authToken, UserDetails userDetails) {
        final String username = getUserNameFromToken(authToken);
        return (username.equals(userDetails.getUsername()) && isTokenExpired(authToken));
    }

    private Map<String, Object> setClaims(AdminUser user, UUID sessionId) {
        Map<String, Object> claimsMap = new HashMap<>();

        claimsMap.put("username", user.getEmail());
        claimsMap.put("roles", Set.of("ADMIN"));
        claimsMap.put("session_id", sessionId);

        return claimsMap;
    }

    private Claims getClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private boolean isTokenExpired(String token) {
        return getExpirationFromToken(token).before(new Date());
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(eKycVerificationSummaryConfig.getJwt().getSecretKey().getBytes(StandardCharsets.UTF_8));
    }
}
