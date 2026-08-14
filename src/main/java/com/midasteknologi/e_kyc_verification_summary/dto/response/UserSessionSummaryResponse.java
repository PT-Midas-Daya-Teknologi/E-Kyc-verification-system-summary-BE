package com.midasteknologi.e_kyc_verification_summary.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSessionSummaryResponse {

    private UUID sessionId;

    private String sessionName;

    private String status;

    private String reason;

    private UserDocumentResponse userDocumentResponse;

    private UserVideoResponse userVideoResponse;

    private String attempts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserDocumentResponse {

        private UUID documentId;

        private String documentName;

        private String ocrData;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserVideoResponse {

        private UUID videoId;

        private String videoName;
    }
}
