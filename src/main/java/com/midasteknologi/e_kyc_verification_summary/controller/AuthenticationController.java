package com.midasteknologi.e_kyc_verification_summary.controller;

import com.midasteknologi.e_kyc_verification_summary.dto.request.AuthenticationRequest;
import com.midasteknologi.e_kyc_verification_summary.exception.CustomException;
import com.midasteknologi.e_kyc_verification_summary.service.AuthenticationService;
import com.midasteknologi.e_kyc_verification_summary.util.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final ResponseUtil respUtil;
    private final AuthenticationService authenticationService;

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@Valid @RequestBody AuthenticationRequest authenticationRequest, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws CustomException {
        return ResponseEntity.ok(
                respUtil.buildBody(
                        authenticationService.authenticate(authenticationRequest, httpServletRequest, httpServletResponse)
                )
        );
    }
}
