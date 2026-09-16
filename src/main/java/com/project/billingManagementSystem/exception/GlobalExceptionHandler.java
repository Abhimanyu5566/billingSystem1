package com.project.billingManagementSystem.exception;

import com.project.billingManagementSystem.apiResponse.APIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle @Valid validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse<Map<String, String>>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().
                forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        APIResponse<Map<String, String>> response = APIResponse.<Map<String, String>>builder().success(false).code(HttpStatus.BAD_REQUEST.value()).message("Validation failed").data(errors).build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle IllegalStateException
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<APIResponse<Object>> handleIllegalStateException(IllegalStateException ex) {

        HttpStatus status;

        String message = ex.getMessage();

        if (message != null && message.contains("already exists")) {
            status = HttpStatus.CONFLICT;
        } else if (message != null && message.contains("not found")) {
            status = HttpStatus.NOT_FOUND;
        } else {
            status = HttpStatus.BAD_REQUEST;
        }

        APIResponse<Object> response = APIResponse.builder().success(false).code(status.value()).message(message).data(null).build();

        return ResponseEntity.status(status).body(response);
    }



    /**
     * Handle IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<APIResponse<Object>> handleIllegalArgumentException(IllegalArgumentException ex) {

        APIResponse<Object> response = APIResponse.builder().success(false).code(HttpStatus.BAD_REQUEST.value()).message(ex.getMessage()).data(null).build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }




    /**
     * Handle all unexpected exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse<Object>> handleException(Exception ex) {

        APIResponse<Object> response = APIResponse.builder().success(false).code(HttpStatus.INTERNAL_SERVER_ERROR.value()).message("Something went wrong.").data(null).build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

