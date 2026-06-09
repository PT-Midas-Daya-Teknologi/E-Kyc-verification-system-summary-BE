package com.midasteknologi.e_kyc_verification_summary.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.midasteknologi.e_kyc_verification_summary.dto.request.VideoRequest;
import com.midasteknologi.e_kyc_verification_summary.exception.CustomException;
import com.midasteknologi.e_kyc_verification_summary.service.VideoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    @PostMapping("/video")
    public ResponseEntity<?> getVideo(@Valid @RequestBody VideoRequest videoRequest) throws CustomException {
        return videoService.getVideo(videoRequest.getVideoId());
    }
}
