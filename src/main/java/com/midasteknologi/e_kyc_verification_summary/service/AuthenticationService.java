package com.midasteknologi.e_kyc_verification_summary.service;

import com.midasteknologi.e_kyc_verification_summary.dto.request.AuthenticationRequest;
import com.midasteknologi.e_kyc_verification_summary.dto.response.AuthenticationResponse;
import com.midasteknologi.e_kyc_verification_summary.dto.response.LogoutResponse;
import com.midasteknologi.e_kyc_verification_summary.exception.CustomException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthenticationService {
    AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws CustomException;

    LogoutResponse logout(HttpServletRequest request, HttpServletResponse response);
}
