package com.midasteknologi.e_kyc_verification_summary.filter;

import com.midasteknologi.e_kyc_verification_summary.config.EKycVerificationSummaryConfig;
import com.midasteknologi.e_kyc_verification_summary.constants.GlobalConstant;
import com.midasteknologi.e_kyc_verification_summary.dto.response.CustomError;
import com.midasteknologi.e_kyc_verification_summary.service.AdminUserSessionService;
import com.midasteknologi.e_kyc_verification_summary.service.JwtService;
import com.midasteknologi.e_kyc_verification_summary.util.ResponseUtil;
import com.midasteknologi.e_kyc_verification_summary.util.SystemContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final ResponseUtil responseUtil;
    private final ObjectMapper objectMapper;
    private final UserDetailsService userDetailsService;
    private final AdminUserSessionService adminUserSessionService;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final SecurityContextRepository securityContextRepository;
    private final EKycVerificationSummaryConfig eKycVerificationSummaryConfig;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("Inside doFilterInternal of [{}]", getClass().getName());

        String requestURI = request.getRequestURI();
        log.info("list {}", String.join(",", eKycVerificationSummaryConfig.getAllowedUrls()));
        if (eKycVerificationSummaryConfig.getAllowedUrls().contains(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.info("Authorization token is null or does not start with Bearer");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                CustomError customError = CustomError
                        .builder()
                        .code(GlobalConstant.UNAUTHORIZE_ERROR_CODE)
                        .message(GlobalConstant.UNAUTHORIZE_ERROR_MESSAGE)
                        .type(GlobalConstant.UNAUTHORIZE_ERROR_TYPE)
                        .build();
                response.getWriter().write(objectMapper.writeValueAsString(
                        responseUtil.buildErrors(customError)
                ));
                return;
            }

            final String authToken = authHeader.substring(7);
            String username = jwtService.getUserNameFromToken(authToken);
            UUID sessionId = jwtService.getSessionIdFromToken(authToken);
            UUID contextSessionId = (UUID) SystemContext.getContext(GlobalConstant.SESSION_ID);

            if (username != null && adminUserSessionService.validateSession(sessionId) && sessionId != contextSessionId  && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtService.validateToken(authToken, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContext securityContext = SecurityContextHolder.getContext();
                    securityContext.setAuthentication(authenticationToken);
                    securityContextRepository.saveContext(securityContext, request, response);
                    SystemContext.setContext(GlobalConstant.SESSION_ID, sessionId);
                }
            }

            log.info("Exiting doFilterInternal of [{}]", getClass().getName());
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            handlerExceptionResolver.resolveException(request, response, null, e);
        }
    }
}
