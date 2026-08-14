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

            String contentType = java.nio.file.Files.probeContentType(
                    java.nio.file.Paths.get(filename)
            );

            if (contentType == null) {
                String extension = "";
                int lastDotIndex = filename.lastIndexOf('.');

                if (lastDotIndex > -1) {
                    extension = filename.substring(lastDotIndex + 1).toLowerCase();
                }

                contentType = switch (extension) {
                    case "jpg", "jpeg" -> MediaType.IMAGE_JPEG_VALUE;
                    case "png" -> MediaType.IMAGE_PNG_VALUE;
                    case "gif" -> MediaType.IMAGE_GIF_VALUE;
                    case "webp" -> "image/webp";
                    case "pdf" -> MediaType.APPLICATION_PDF_VALUE;
                    default -> MediaType.APPLICATION_OCTET_STREAM_VALUE;
                };
            }

            log.info("Returning document with contentType: {}", contentType);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(document.getContent());

        } catch (Exception e) {
            log.error("Exception in getDocument()", e);

            if (e instanceof CustomException customException) {
                throw customException;
            }

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
                                            .sessionName(userSession.getSessionName())
                                            .status(userSession.getStatus())
                                            .reason(userSession.getReason())
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
                                            .userVideoResponse(
                                                    userSession.getUserVideo() != null ?
                                                            UserSessionSummaryResponse.UserVideoResponse
                                                                    .builder()
                                                                    .videoId(userSession.getUserVideo().getId())
                                                                    .videoName(userSession.getUserVideo().getName())
                                                                    .build()
                                                            : null)
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
