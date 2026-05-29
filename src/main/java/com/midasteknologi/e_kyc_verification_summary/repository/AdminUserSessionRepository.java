package com.midasteknologi.e_kyc_verification_summary.repository;

import com.midasteknologi.e_kyc_verification_summary.entity.AdminUserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdminUserSessionRepository extends JpaRepository<AdminUserSession, UUID> {
    Optional<AdminUserSession> findByIdAndIsActiveTrue(UUID sessionId);

    int countByIdAndExpiryIsGreaterThanAndIsActiveTrue(UUID sessionId, LocalDateTime now);
}
