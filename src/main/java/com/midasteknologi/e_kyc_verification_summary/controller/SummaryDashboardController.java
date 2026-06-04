package com.midasteknologi.e_kyc_verification_summary.controller;

import com.midasteknologi.e_kyc_verification_summary.dto.request.DocumentRequest;
import com.midasteknologi.e_kyc_verification_summary.dto.request.PaginatedRequest;
import com.midasteknologi.e_kyc_verification_summary.dto.request.UserSessionSummaryRequest;
import com.midasteknologi.e_kyc_verification_summary.exception.CustomException;
import com.midasteknologi.e_kyc_verification_summary.service.SummaryService;
import com.midasteknologi.e_kyc_verification_summary.util.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class SummaryDashboardController {

    private final ResponseUtil responseUtil;
    private final SummaryService summaryService;

    @PostMapping("/document")
    public ResponseEntity<?> getDocument(@Valid @RequestBody DocumentRequest documentRequest) throws CustomException {
        return summaryService.getDocument(documentRequest.getDocumentId());
    }

    @PostMapping("/summary")
    public ResponseEntity<?> getSummary(@Valid @RequestBody PaginatedRequest paginatedRequest) throws CustomException {
        return ResponseEntity.ok(
                responseUtil.buildBody(
                        summaryService.getSummary(paginatedRequest)
                )
        );
    }

    @PostMapping("/summary/session")
    public ResponseEntity<?> getSummarySession(@Valid @RequestBody UserSessionSummaryRequest userSessionSummaryRequest) throws CustomException {
        return ResponseEntity.ok(
                responseUtil.buildBody(
                        summaryService.getSummarySession(userSessionSummaryRequest)
                )
        );
    }
    
   
}
