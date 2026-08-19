package com.midasteknologi.e_kyc_verification_summary.repository;

import com.midasteknologi.e_kyc_verification_summary.entity.UserSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, UUID>  {

    Page<UserSession> findAllByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    java.util.List<UserSession> findByUserVideoId(UUID videoId);
}
