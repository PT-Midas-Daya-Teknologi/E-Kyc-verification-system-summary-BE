package com.midasteknologi.e_kyc_verification_summary.controller;

import com.midasteknologi.e_kyc_verification_summary.dto.request.AudioRequest;
import com.midasteknologi.e_kyc_verification_summary.exception.CustomException;
import com.midasteknologi.e_kyc_verification_summary.service.AudioService;
import com.midasteknologi.e_kyc_verification_summary.util.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class AudioController {

    private final ResponseUtil responseUtil;
    private final AudioService audioService;

    @PostMapping("/audio")
    public ResponseEntity<?> getAudio(@Valid @RequestBody AudioRequest audioRequest) throws CustomException {
        return ResponseEntity.ok(
                responseUtil.buildBody(
                        audioService.getAudioPath(audioRequest.getVideoId())
                )
        );
    }

    @PostMapping("/audio/file")
    public ResponseEntity<?> getAudioFile(@Valid @RequestBody AudioRequest audioRequest, HttpServletRequest request) throws CustomException {
        return audioService.getAudioFile(audioRequest.getVideoId(), request.getHeader(HttpHeaders.RANGE));
    }
}
