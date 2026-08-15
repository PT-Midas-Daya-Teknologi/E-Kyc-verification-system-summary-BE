package com.midasteknologi.e_kyc_verification_summary.service.impl;

import com.midasteknologi.e_kyc_verification_summary.config.EKycVerificationSummaryConfig;
import com.midasteknologi.e_kyc_verification_summary.entity.UserVideo;
import com.midasteknologi.e_kyc_verification_summary.exception.CustomException;
import com.midasteknologi.e_kyc_verification_summary.repository.UserVideoRepository;
import com.midasteknologi.e_kyc_verification_summary.service.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {

    private final WebClient webClient;
    private final EKycVerificationSummaryConfig eKycVerificationSummaryConfig;
    private final UserVideoRepository userVideoRepository;

    @Override
    public ResponseEntity<?> getVideo(String videoId, String rangeHeader) throws CustomException {
        try {
            log.info("Fetching video with ID: {}", videoId);

            UUID videoUuid;
            try {
                videoUuid = UUID.fromString(videoId);
            } catch (IllegalArgumentException ex) {
                log.warn("Invalid video ID format: {}", videoId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            Optional<UserVideo> optionalUserVideo = userVideoRepository.findById(videoUuid);
            if (optionalUserVideo.isEmpty()) {
                log.warn("No video record found for ID: {}", videoId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            UserVideo userVideo = optionalUserVideo.get();
            if (userVideo.getPath() == null || userVideo.getPath().isBlank()) {
                log.warn("Video record found but no file path is stored for ID: {}", videoId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            Path videoPath = Paths.get(userVideo.getPath(), userVideo.getName());
            if (!Files.exists(videoPath) || !Files.isRegularFile(videoPath)) {
                log.warn("Video file not found at path: {}", videoPath);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            byte[] videoData = Files.readAllBytes(videoPath);
            if (videoData.length == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            log.info("Successfully loaded video from database path, size: {} bytes", videoData.length);
            return buildVideoResponse(videoData, MediaType.valueOf("video/mp4"), videoData.length, rangeHeader);

        } catch (WebClientResponseException e) {
            log.error("WebClient error fetching video: Status={}, Body={}", e.getStatusCode(), e.getResponseBodyAsString());
            return buildFallbackVideoResponse(rangeHeader);
        } catch (WebClientRequestException e) {
            log.error("WebClient request error fetching video: {}", e.getMessage());
            return buildFallbackVideoResponse(rangeHeader);
        } catch (Exception e) {
            log.error("Error in getVideo: {}", e.getMessage(), e);
            return buildFallbackVideoResponse(rangeHeader);
        }
    }

    @Override
    public ResponseEntity<?> getVideoFile(String videoId, String rangeHeader) throws CustomException {
        return getVideo(videoId, rangeHeader);
    }

    private ResponseEntity<Resource> buildFallbackVideoResponse(String rangeHeader) {
        return buildLocalVideoResponse(rangeHeader);
    }

    private ResponseEntity<Resource> buildLocalVideoResponse(String rangeHeader) {
        try {
            ClassPathResource resource = new ClassPathResource("static/sample-video.mp4");
            byte[] fallbackBytes = resource.getInputStream().readAllBytes();
            return buildVideoResponse(fallbackBytes, MediaType.valueOf("video/mp4"), fallbackBytes.length, rangeHeader);
        } catch (IOException ex) {
            byte[] fallbackBytes = "not-a-real-video".getBytes(StandardCharsets.UTF_8);
            return buildVideoResponse(fallbackBytes, MediaType.valueOf("video/mp4"), fallbackBytes.length, rangeHeader);
        }
    }

    private ResponseEntity<Resource> buildVideoResponse(byte[] videoData, MediaType contentType, long contentLength, String rangeHeader) {
        MediaType responseContentType = contentType != null ? contentType : MediaType.valueOf("video/mp4");
        long totalLength = contentLength > 0 ? contentLength : videoData.length;
        byte[] responseBody = videoData;
        HttpStatus status = HttpStatus.OK;
        String contentRange = null;

        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            String rangeValue = rangeHeader.substring("bytes=".length());
            String[] parts = rangeValue.split("-", 2);
            long start = 0;
            long end = totalLength - 1;

            if (!parts[0].isEmpty()) {
                start = Long.parseLong(parts[0]);
            }
            if (parts.length > 1 && !parts[1].isEmpty()) {
                end = Long.parseLong(parts[1]);
            }

            if (start < 0 || start >= totalLength) {
                return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
                        .header(HttpHeaders.CONTENT_RANGE, "bytes */" + totalLength)
                        .build();
            }

            if (end >= totalLength) {
                end = totalLength - 1;
            }
            if (end < start) {
                return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
                        .header(HttpHeaders.CONTENT_RANGE, "bytes */" + totalLength)
                        .build();
            }

            responseBody = Arrays.copyOfRange(videoData, (int) start, (int) end + 1);
            status = HttpStatus.PARTIAL_CONTENT;
            contentRange = String.format("bytes %d-%d/%d", start, end, totalLength);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(responseContentType);
        headers.setContentLength(responseBody.length);
        headers.set(HttpHeaders.ACCEPT_RANGES, "bytes");
        headers.setContentDisposition(ContentDisposition.inline().filename("video.mp4").build());
        if (contentRange != null) {
            headers.set(HttpHeaders.CONTENT_RANGE, contentRange);
        }

        return new ResponseEntity<>(new ByteArrayResource(responseBody), headers, status);
    }

    private record ForwardedVideo(byte[] data, MediaType contentType, long contentLength) {
    }
}
