package com.midasteknologi.e_kyc_verification_summary.exception;

import com.midasteknologi.e_kyc_verification_summary.constants.GlobalConstant;
import com.midasteknologi.e_kyc_verification_summary.dto.response.CustomError;
import com.midasteknologi.e_kyc_verification_summary.util.ResponseUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    protected final ResponseUtil responseUtil;

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        logger.info("----- GlobalExceptionHandler handling handleMethodArgumentNotValid -----");
        logger.error("Exception ", ex);

        List<String> errors = new ArrayList<>();

        for (FieldError fieldError: ex.getFieldErrors()) {
            errors.add(
                    String.format("%s - %s", fieldError.getField(), fieldError.getDefaultMessage())
            );
        }

        return ResponseEntity.badRequest().body(
                responseUtil.buildErrors(
                        CustomError
                                .builder()
                                .code(GlobalConstant.BAD_REQUEST_ERROR_CODE)
                                .type(GlobalConstant.BAD_REQUEST_ERROR_TYPE)
                                .message(String.valueOf(errors))
                                .build()
                )
        );
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        logger.info("----- GlobalExceptionHandler handling handleTypeMismatch -----");
        logger.error("Exception ", ex);

        return ResponseEntity
                .badRequest()
                .body(
                        responseUtil.buildErrors(
                                CustomError
                                        .builder()
                                        .code(GlobalConstant.BAD_REQUEST_ERROR_CODE)
                                        .type(GlobalConstant.BAD_REQUEST_ERROR_TYPE)
                                        .message(ex.getLocalizedMessage())
                                        .build()
                        )
                );
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        logger.info("----- GlobalExceptionHandler handling handleTypeMismatch -----");
        logger.error("Exception ", ex);

        String errorMessage = String.format("%s should be of type %s", ex.getPropertyName(), ex.getRequiredType());
        return ResponseEntity
                .badRequest()
                .body(
                        responseUtil.buildErrors(
                                CustomError
                                        .builder()
                                        .code(GlobalConstant.BAD_REQUEST_ERROR_CODE)
                                        .type(GlobalConstant.BAD_REQUEST_ERROR_TYPE)
                                        .message(errorMessage)
                                        .build()
                        )
                );
    }

    @ExceptionHandler(value = BadCredentialsException.class)
    private ResponseEntity<Object> handleBadCredentialsException(Exception ex) {
        logger.info("----- GlobalExceptionHandler handling handleException -----");
        logger.error("Exception ", ex);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        responseUtil.buildErrors(
                                CustomError
                                        .builder()
                                        .code(GlobalConstant.BAD_CREDENTIALS_ERROR_CODE)
                                        .type(GlobalConstant.BAD_CREDENTIALS_ERROR_TYPE)
                                        .message(GlobalConstant.BAD_CREDENTIALS_ERROR_MESSAGE)
                                        .build()
                        )
                );
    }

    @ExceptionHandler(value = ExpiredJwtException.class)
    private ResponseEntity<Object> handleExpiredJwtException(ExpiredJwtException ex) {
        logger.info("----- GlobalExceptionHandler handling handleExpiredJwtException -----");
        logger.error("Exception ", ex);

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        responseUtil.buildErrors(
                                CustomError
                                        .builder()
                                        .code(GlobalConstant.UNAUTHORIZE_ERROR_CODE)
                                        .type(GlobalConstant.UNAUTHORIZE_ERROR_MESSAGE)
                                        .message(GlobalConstant.UNAUTHORIZE_ERROR_TYPE)
                                        .build()
                        )
                );
    }

    @ExceptionHandler(value = CustomException.class)
    private ResponseEntity<Object> handleCustomException(CustomException ex) {
        logger.info("----- GlobalExceptionHandler handling handleException -----");
        logger.error("Exception ", ex);

        CustomException exception = (CustomException) ex;
        return ResponseEntity
                .internalServerError()
                .body(
                        responseUtil.buildErrors(
                                CustomError
                                        .builder()
                                        .code(exception.getCode())
                                        .type(exception.getMessage())
                                        .message(exception.getType())
                                        .build()
                        )
                );
    }
}
