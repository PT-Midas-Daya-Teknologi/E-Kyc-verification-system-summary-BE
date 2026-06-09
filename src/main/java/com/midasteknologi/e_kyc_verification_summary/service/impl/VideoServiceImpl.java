package com.midasteknologi.e_kyc_verification_summary.service.impl;

import com.midasteknologi.e_kyc_verification_summary.config.EKycVerificationSummaryConfig;
import com.midasteknologi.e_kyc_verification_summary.exception.CustomException;
import com.midasteknologi.e_kyc_verification_summary.service.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {

    private final WebClient webClient;
    private final EKycVerificationSummaryConfig eKycVerificationSummaryConfig;

    @Override
    public ResponseEntity<?> getVideo(String videoId) throws CustomException {
        try {
            log.info("Fetching video with ID: {}", videoId);

            // Prepare the request body
            String requestBody = String.format("{\"video_id\": \"%s\"}", videoId);

            // Call the Python backend
            byte[] videoData = webClient
                    .post()
                    .uri(eKycVerificationSummaryConfig.getPythonServiceConfig().getVideoDownloadUrl())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(requestBody))
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .doOnError(error -> log.error("Error fetching video from Python service: {}", error.getMessage()))
                    .block();

            if (videoData == null || videoData.length == 0) {
                throw new CustomException("404", "Video not found for the given ID", "NOT_FOUND");
            }

            log.info("Successfully fetched video, size: {} bytes", videoData.length);

            // Return the video as a streaming response
            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf("video/mp4"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"video.mp4\"")
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(videoData.length))
                    .body(new ByteArrayResource(videoData));

        } catch (WebClientResponseException e) {
            log.error("WebClient error fetching video: Status={}, Body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new CustomException("500", "Failed to fetch video from Python service: " + e.getMessage(), "EXTERNAL_SERVICE_ERROR");
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error in getVideo: {}", e.getMessage(), e);
            throw new CustomException("500", "Error fetching video: " + e.getMessage(), "INTERNAL_ERROR");
        }
    }
}
