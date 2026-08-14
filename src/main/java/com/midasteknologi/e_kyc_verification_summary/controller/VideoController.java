package com.midasteknologi.e_kyc_verification_summary.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
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
    public ResponseEntity<?> getVideo(@Valid @RequestBody VideoRequest videoRequest, HttpServletRequest request) throws CustomException {
        return videoService.getVideo(videoRequest.getVideoId(), request.getHeader(HttpHeaders.RANGE));
    }

    @PostMapping("/video/file")
    public ResponseEntity<?> getVideoFile(@Valid @RequestBody VideoRequest videoRequest, HttpServletRequest request) throws CustomException {
        return videoService.getVideoFile(videoRequest.getVideoId(), request.getHeader(HttpHeaders.RANGE));
    }
}
