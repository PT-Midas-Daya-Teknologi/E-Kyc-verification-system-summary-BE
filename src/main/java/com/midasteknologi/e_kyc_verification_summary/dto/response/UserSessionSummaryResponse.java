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

    private UserDocumentResponse userDocumentResponse;

    private UUID videoId;

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
}
