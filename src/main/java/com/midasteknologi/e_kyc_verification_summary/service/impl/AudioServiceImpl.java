package com.midasteknologi.e_kyc_verification_summary.service.impl;

import com.midasteknologi.e_kyc_verification_summary.dto.response.AudioResponse;
import com.midasteknologi.e_kyc_verification_summary.entity.UserAudio;
import com.midasteknologi.e_kyc_verification_summary.entity.UserSession;
import com.midasteknologi.e_kyc_verification_summary.exception.CustomException;
import com.midasteknologi.e_kyc_verification_summary.repository.UserAudioRepository;
import com.midasteknologi.e_kyc_verification_summary.repository.UserSessionRepository;
import com.midasteknologi.e_kyc_verification_summary.service.AudioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AudioServiceImpl implements AudioService {

    private final UserSessionRepository userSessionRepository;
    private final UserAudioRepository userAudioRepository;

    @Override
    public AudioResponse getAudioPath(String videoId) throws CustomException {
        try {
            log.info("Fetching audio path for video ID: {}", videoId);

            UUID videoUuid;
            try {
                videoUuid = UUID.fromString(videoId);
            } catch (IllegalArgumentException ex) {
                log.warn("Invalid video ID format: {}", videoId);
                throw new CustomException("400", "Invalid video ID format", "BadRequest");
            }

            // Step 1: Find the session by video_id
            UserSession userSession = userSessionRepository.findByUserVideoId(videoUuid);
            if (userSession == null) {
                log.warn("No session record found with video_id: {}", videoId);
                throw new CustomException("404", "No session record found", "NotFound");
            }

            // Step 2: Get the audio_id from the session
            if (userSession.getUserAudio() == null || userSession.getUserAudio().getId() == null) {
                log.warn("Session found but no audio_id is stored for video ID: {}", videoId);
                throw new CustomException("404", "No audio record found", "NotFound");
            }

            // Step 3: Fetch the audio record
            UserAudio userAudio = userAudioRepository.findById(userSession.getUserAudio().getId()).orElse(null);
            if (userAudio == null || userAudio.getPath() == null || userAudio.getPath().isBlank()) {
                log.warn("Audio record found but no path is stored for video ID: {}", videoId);
                throw new CustomException("404", "No audio path found", "NotFound");
            }

            log.info("Successfully loaded audio path from database for video ID: {}", videoId);
            return AudioResponse.builder().audioPath(userAudio.getPath()).build();
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error in getAudioPath: {}", e.getMessage(), e);
            throw new CustomException("500", "Failed to load audio path", "InternalServerError");
        }
    }

    @Override
    public ResponseEntity<?> getAudioFile(String videoId, String rangeHeader) throws CustomException {
        try {
            log.info("Fetching audio file with video ID: {}", videoId);

            UUID videoUuid;
            try {
                videoUuid = UUID.fromString(videoId);
            } catch (IllegalArgumentException ex) {
                log.warn("Invalid video ID format: {}", videoId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            // Step 1: Find the session by video_id
            UserSession userSession = userSessionRepository.findByUserVideoId(videoUuid);
            if (userSession == null) {
                log.warn("No session record found with video_id: {}", videoId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // Step 2: Get the audio_id from the session
            if (userSession.getUserAudio() == null || userSession.getUserAudio().getId() == null) {
                log.warn("Session found but no audio_id is stored for video ID: {}", videoId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // Step 3: Fetch the audio record
            UserAudio userAudio = userAudioRepository.findById(userSession.getUserAudio().getId()).orElse(null);
            if (userAudio == null || userAudio.getPath() == null || userAudio.getPath().isBlank()) {
                log.warn("Audio record found but no path is stored for video ID: {}", videoId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // Step 4: Read the audio file from the path
            Path audioPath = Paths.get(userAudio.getPath(), userAudio.getName());
            if (!Files.exists(audioPath) || !Files.isRegularFile(audioPath)) {
                log.warn("Audio file not found at path: {}", audioPath);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            byte[] audioData = Files.readAllBytes(audioPath);
            if (audioData.length == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // Step 5: Resolve media type and build response with byte-range support
            MediaType mediaType = resolveAudioMediaType(audioPath);
            log.info("Successfully loaded audio from database path, size: {} bytes, mediaType: {}", audioData.length, mediaType);
            return buildAudioResponse(audioData, mediaType, audioData.length, rangeHeader);
        } catch (IOException e) {
            log.error("Error reading audio file for video ID {}: {}", videoId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Error in getAudioFile: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private MediaType resolveAudioMediaType(Path audioPath) {
        String fileName = audioPath.getFileName() != null ? audioPath.getFileName().toString().toLowerCase() : "";
        if (fileName.endsWith(".ogg")) {
            return MediaType.valueOf("audio/ogg");
        }
        if (fileName.endsWith(".mp3")) {
            return MediaType.valueOf("audio/mpeg");
        }
        if (fileName.endsWith(".wav")) {
            return MediaType.valueOf("audio/wav");
        }
        if (fileName.endsWith(".m4a") || fileName.endsWith(".mp4")) {
            return MediaType.valueOf("audio/mp4");
        }
        return MediaType.APPLICATION_OCTET_STREAM;
    }

    private ResponseEntity<Resource> buildAudioResponse(byte[] audioData, MediaType contentType, long contentLength, String rangeHeader) {
        MediaType responseContentType = contentType != null ? contentType : MediaType.APPLICATION_OCTET_STREAM;
        long totalLength = contentLength > 0 ? contentLength : audioData.length;
        byte[] responseBody = audioData;
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

            responseBody = Arrays.copyOfRange(audioData, (int) start, (int) end + 1);
            status = HttpStatus.PARTIAL_CONTENT;
            contentRange = String.format("bytes %d-%d/%d", start, end, totalLength);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(responseContentType);
        headers.setContentLength(responseBody.length);
        headers.set(HttpHeaders.ACCEPT_RANGES, "bytes");
        headers.setContentDisposition(ContentDisposition.inline().filename("audio-file").build());
        if (contentRange != null) {
            headers.set(HttpHeaders.CONTENT_RANGE, contentRange);
        }

        return new ResponseEntity<>(new ByteArrayResource(responseBody), headers, status);
    }
}
