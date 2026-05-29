package com.midasteknologi.e_kyc_verification_summary.service;

import com.midasteknologi.e_kyc_verification_summary.dto.request.PaginatedRequest;
import com.midasteknologi.e_kyc_verification_summary.dto.request.UserSessionSummaryRequest;
import com.midasteknologi.e_kyc_verification_summary.dto.response.PaginatedResponse;

public interface SummaryService {
    PaginatedResponse getSummary(PaginatedRequest paginatedRequest);

    PaginatedResponse getSummarySession(UserSessionSummaryRequest userSessionSummaryRequest);
}
