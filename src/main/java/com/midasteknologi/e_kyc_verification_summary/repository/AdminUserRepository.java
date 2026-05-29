package com.midasteknologi.e_kyc_verification_summary.repository;

import com.midasteknologi.e_kyc_verification_summary.entity.AdminUser;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, UUID> {
    Optional<AdminUser> findByEmail(@NotBlank String email);
}
