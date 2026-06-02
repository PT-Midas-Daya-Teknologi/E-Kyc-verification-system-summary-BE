package com.midasteknologi.e_kyc_verification_summary.service.impl;

import com.midasteknologi.e_kyc_verification_summary.constants.GlobalConstant;
import com.midasteknologi.e_kyc_verification_summary.dto.request.PaginatedRequest;
import com.midasteknologi.e_kyc_verification_summary.dto.request.UserSessionSummaryRequest;
import com.midasteknologi.e_kyc_verification_summary.dto.response.PaginatedResponse;
import com.midasteknologi.e_kyc_verification_summary.dto.response.UserSessionSummaryResponse;
import com.midasteknologi.e_kyc_verification_summary.entity.User;
import com.midasteknologi.e_kyc_verification_summary.entity.UserSession;
import com.midasteknologi.e_kyc_verification_summary.exception.CustomException;
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
    public PaginatedResponse getSummary(PaginatedRequest paginatedRequest) throws CustomException {
        log.info("Inside getSummary()");

        try {
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
        } catch (Exception e) {
            log.error("Exception in getSummary()", e);
            throw new CustomException(
                    GlobalConstant.INTERNAL_SERVER_ERROR_CODE,
                    GlobalConstant.INTERNAL_SERVER_ERROR_MESSAGE,
                    GlobalConstant.INTERNAL_SERVER_ERROR_TYPE
            );
        }
    }

    @Override
    public PaginatedResponse getSummarySession(UserSessionSummaryRequest userSessionSummaryRequest) throws CustomException {
        log.info("Inside getSummarySession()");

        try {
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
                                            .userDocumentResponse(
                                                    userSession.getUserDocument() != null ?
                                                            UserSessionSummaryResponse.UserDocumentResponse
                                                                    .builder()
                                                                    .documentId(userSession.getUserDocument().getId())
                                                                    .documentName(userSession.getUserDocument().getName())
                                                                    .ocrData(userSession.getUserDocument().getOcrData())
                                                                    .build()
                                                            : null
                                            )
                                            .videoId(userSession.getVideoId())
                                            .attempts(userSession.getAttempts())
                                            .build()
                            ).toList())
                    .build();
        } catch (Exception e) {
            log.error("Exception in getSummarySession()", e);
            throw new CustomException(
                    GlobalConstant.INTERNAL_SERVER_ERROR_CODE,
                    GlobalConstant.INTERNAL_SERVER_ERROR_MESSAGE,
                    GlobalConstant.INTERNAL_SERVER_ERROR_TYPE
            );
        }
    }
}
