package com.midasteknologi.e_kyc_verification_summary.service;

import com.midasteknologi.e_kyc_verification_summary.dto.request.PaginatedRequest;
import com.midasteknologi.e_kyc_verification_summary.dto.request.UserSessionSummaryRequest;
import com.midasteknologi.e_kyc_verification_summary.dto.response.PaginatedResponse;
import com.midasteknologi.e_kyc_verification_summary.exception.CustomException;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface SummaryService {
    PaginatedResponse getSummary(PaginatedRequest paginatedRequest) throws CustomException;

    PaginatedResponse getSummarySession(UserSessionSummaryRequest userSessionSummaryRequest) throws CustomException;

    ResponseEntity<byte[]> getDocument(UUID documentId) throws CustomException;
}
