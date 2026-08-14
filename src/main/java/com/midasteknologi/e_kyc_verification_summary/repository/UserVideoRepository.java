package com.midasteknologi.e_kyc_verification_summary.repository;

import com.midasteknologi.e_kyc_verification_summary.entity.UserVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserVideoRepository extends JpaRepository<UserVideo, UUID> {
}
