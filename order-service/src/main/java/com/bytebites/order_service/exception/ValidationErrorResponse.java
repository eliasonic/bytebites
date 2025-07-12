package com.bytebites.order_service.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidationErrorResponse extends ErrorResponse {
    private Map<String, String> fieldErrors;

    public ValidationErrorResponse(LocalDateTime timestamp, String message, String details, String errorCode, Map<String, String> fieldErrors) {
        super(timestamp, message, details, errorCode);
        this.fieldErrors = fieldErrors;
    }
}