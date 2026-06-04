package com.midasteknologi.e_kyc_verification_summary.service.impl;

import com.midasteknologi.e_kyc_verification_summary.constants.GlobalConstant;
import com.midasteknologi.e_kyc_verification_summary.dto.request.PaginatedRequest;
import com.midasteknologi.e_kyc_verification_summary.dto.request.UserSessionSummaryRequest;
import com.midasteknologi.e_kyc_verification_summary.dto.response.PaginatedResponse;
import com.midasteknologi.e_kyc_verification_summary.dto.response.UserSessionSummaryResponse;
import com.midasteknologi.e_kyc_verification_summary.entity.User;
import com.midasteknologi.e_kyc_verification_summary.entity.UserDocument;
import com.midasteknologi.e_kyc_verification_summary.entity.UserSession;
import com.midasteknologi.e_kyc_verification_summary.exception.CustomException;
import com.midasteknologi.e_kyc_verification_summary.repository.UserDocumentRepository;
import com.midasteknologi.e_kyc_verification_summary.repository.UserRepository;
import com.midasteknologi.e_kyc_verification_summary.repository.UserSessionRepository;
import com.midasteknologi.e_kyc_verification_summary.service.SummaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SummaryServiceImpl implements SummaryService {

    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;
    private final UserDocumentRepository userDocumentRepository;

    @Override
    public ResponseEntity<byte[]> getDocument(UUID documentId) throws CustomException {
        log.info("Inside getDocument() for documentId: {}", documentId);

        try {
            UserDocument document = userDocumentRepository.findById(documentId)
                    .orElseThrow(() -> new CustomException(
                            GlobalConstant.NOT_FOUND_ERROR_CODE,
                            GlobalConstant.NOT_FOUND_ERROR_MESSAGE,
                            GlobalConstant.NOT_FOUND_ERROR_TYPE
                    ));

            String filename = (document.getName() != null && !document.getName().isBlank())
                    ? document.getName()
                    : documentId.toString();

            String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;

            try {
                String type = document.getType();

                // Use type only if it is a valid MIME type (contains '/')
                if (type != null && !type.isBlank() && type.contains("/")) {
                    contentType = type;
                } else {
                    // Detect MIME type from file name
                    contentType = java.nio.file.Files.probeContentType(
                            java.nio.file.Paths.get(filename)
                    );

                    if (contentType == null) {
                        String lowerFileName = filename.toLowerCase();

                        if (lowerFileName.endsWith(".jpg") || lowerFileName.endsWith(".jpeg")) {
                            contentType = MediaType.IMAGE_JPEG_VALUE;
                        } else if (lowerFileName.endsWith(".png")) {
                            contentType = MediaType.IMAGE_PNG_VALUE;
                        } else if (lowerFileName.endsWith(".gif")) {
                            contentType = MediaType.IMAGE_GIF_VALUE;
                        } else if (lowerFileName.endsWith(".webp")) {
                            contentType = "image/webp";
                        } else if (lowerFileName.endsWith(".pdf")) {
                            contentType = MediaType.APPLICATION_PDF_VALUE;
                        } else {
                            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("Could not determine content type. Using application/octet-stream", e);
            }

            log.info("Returning document with contentType: {}", contentType);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(document.getContent());

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception in getDocument()", e);
            throw new CustomException(
                    GlobalConstant.INTERNAL_SERVER_ERROR_CODE,
                    GlobalConstant.INTERNAL_SERVER_ERROR_MESSAGE,
                    GlobalConstant.INTERNAL_SERVER_ERROR_TYPE
            );
        }
    }

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
