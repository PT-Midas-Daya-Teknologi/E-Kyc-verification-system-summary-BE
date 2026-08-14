package com.midasteknologi.e_kyc_verification_summary.service;

import com.midasteknologi.e_kyc_verification_summary.dto.response.AudioResponse;
import com.midasteknologi.e_kyc_verification_summary.exception.CustomException;
import org.springframework.http.ResponseEntity;

public interface AudioService {
    AudioResponse getAudioPath(String videoId) throws CustomException;

    ResponseEntity<?> getAudioFile(String videoId, String rangeHeader) throws CustomException;
}
