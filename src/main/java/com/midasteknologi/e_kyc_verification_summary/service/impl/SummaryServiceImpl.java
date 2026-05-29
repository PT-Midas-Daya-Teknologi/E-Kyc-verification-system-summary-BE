package com.midasteknologi.e_kyc_verification_summary.service.impl;

import com.midasteknologi.e_kyc_verification_summary.dto.request.PaginatedRequest;
import com.midasteknologi.e_kyc_verification_summary.dto.request.UserSessionSummaryRequest;
import com.midasteknologi.e_kyc_verification_summary.dto.response.PaginatedResponse;
import com.midasteknologi.e_kyc_verification_summary.dto.response.UserSessionSummaryResponse;
import com.midasteknologi.e_kyc_verification_summary.entity.User;
import com.midasteknologi.e_kyc_verification_summary.entity.UserSession;
import com.midasteknologi.e_kyc_verification_summary.repository.UserRepository;
import com.midasteknologi.e_kyc_verification_summary.repository.UserSessionRepository;
import com.midasteknologi.e_kyc_verification_summary.service.SummaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SummaryServiceImpl implements SummaryService {

    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;

    @Override
    public PaginatedResponse getSummary(PaginatedRequest paginatedRequest) {
        log.info("Inside getSummary()");

        Pageable pageable = PageRequest.of(
                paginatedRequest.getPage(),
                paginatedRequest.getSize()
        );

        Page<User> userPage = userRepository.findAll(pageable);

        log.info("Exiting getSummary()");
        return PaginatedResponse
                .builder()
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .page(userPage.getNumber())
                .size(userPage.getSize())
                .data(userPage.getContent())
                .build();
    }

    @Override
    public PaginatedResponse getSummarySession(UserSessionSummaryRequest userSessionSummaryRequest) {
        log.info("Inside getSummarySession()");

        Pageable pageable = PageRequest.of(
                userSessionSummaryRequest.getPage(),
                userSessionSummaryRequest.getSize()
        );

        Page<UserSession> userSessionPage = userSessionRepository.findAllByUserId(userSessionSummaryRequest.getUserId(), pageable);

        log.info("Exiting getSummarySession()");
        return PaginatedResponse
                .builder()
                .totalElements(userSessionPage.getTotalElements())
                .totalPages(userSessionPage.getTotalPages())
                .page(userSessionPage.getNumber())
                .size(userSessionPage.getSize())
                .data(userSessionPage
                        .getContent()
                        .stream()
                        .map(userSession ->
                                UserSessionSummaryResponse
                                        .builder()
                                        .sessionId(userSession.getId())
                                        .documentId(userSession.getUserDocument().getId())
                                        .videoId(userSession.getVideoId())
                                        .ocrData(userSession.getUserDocument().getOcrData())
                                        .attempts(userSession.getAttempts())
                                        .build()
                        ).toList())
                .build();
    }
}
