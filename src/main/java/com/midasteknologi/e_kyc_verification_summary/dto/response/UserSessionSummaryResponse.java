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

    private UUID documentId;

    private UUID videoId;

    private String ocrData;

    private String attempts;
}
