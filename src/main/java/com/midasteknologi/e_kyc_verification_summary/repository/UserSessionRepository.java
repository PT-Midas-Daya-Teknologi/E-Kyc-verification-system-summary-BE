package com.midasteknologi.e_kyc_verification_summary.repository;

import com.midasteknologi.e_kyc_verification_summary.entity.UserSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, String>  {

    Page<UserSession> findAllByUserId(Long userId, Pageable pageable);
}
