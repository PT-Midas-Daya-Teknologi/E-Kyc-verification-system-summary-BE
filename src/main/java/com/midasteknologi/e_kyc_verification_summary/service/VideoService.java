package com.midasteknologi.e_kyc_verification_summary.service;

import com.midasteknologi.e_kyc_verification_summary.exception.CustomException;
import org.springframework.http.ResponseEntity;

public interface VideoService {
    ResponseEntity<?> getVideo(String videoId, String rangeHeader) throws CustomException;

    ResponseEntity<?> getVideoFile(String videoId, String rangeHeader) throws CustomException;
}
