package com.example.eventsystem.exception;

import com.example.eventsystem.dto.ApiResponse;
import jakarta.validation.constraints.Null;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.nio.file.AccessDeniedException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author * Sunnatullayev Mahmudnazar *  * market *  * 12:05 *
 */

@ControllerAdvice
public class GlobalException {
    @Value("${spring.servlet.multipart.max-file-size:10MB}")
    private String maxFileSize;

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public HttpEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return validationException(ex.getBindingResult());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public HttpEntity<?> badCredentialsException(BadCredentialsException e) {
        ApiResponse<Null> response = new ApiResponse<>();
        response.setStatus(403);
        response.setMessage(e.getMessage());
        response.setSuccess(false);
        return ResponseEntity.badRequest().body(response);
    }

    private HttpEntity<?> validationException(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        bindingResult.getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(ApiResponse.<Map<String, String>>builder()
                .message("Data validation error")
                .data(errors)
                .build());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public HttpEntity<?> handleValidationExceptions1(BindException ex) {
        return validationException(ex.getBindingResult());
    }


    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(SQLException.class)
    public HttpEntity<?> handleSqlError(SQLException e) {
        Map<String, String> errors = new LinkedHashMap<>();
        if ("23503".equals(e.getSQLState())) {
            String message = e.getMessage();
            message = message.substring(0, message.length() - 2);
            String obj = message.substring(message.lastIndexOf("\"") + 1);
            String id = message.substring(message.lastIndexOf("(") + 1, message.lastIndexOf(")"));
            errors.put("message", obj + " with id=(" + id + ") not found");
        } else {
            errors.put("message", e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.<Map<String, String>>builder()
                .message("Error data saving")
                .data(errors)
                .build());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public HttpEntity<?> handleSizeException() {
        return ResponseEntity.badRequest().body(ApiResponse.builder()
                .message("File size must be less than " + maxFileSize)
                .build());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public HttpEntity<?> accessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.builder()
                .message(ex.getMessage())
                .build());
    }

    @ExceptionHandler({PropertyReferenceException.class})
    public HttpEntity<?> fieldNameNotFound(PropertyReferenceException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.builder()
                .message("\"" + ex.getPropertyName() + "\" is not found")
                .build());
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public HttpEntity<?> wrongFieldName(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.builder()
                .message(ex.getMessage())
                .build());
    }
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public HttpEntity<?> parseError(){
//        return ResponseEntity.badRequest().body(ApiResponse.builder().message("Something went wrong!!!").success(false).build());
//    }
}
