package com.clouddoc.manager.common;

import com.clouddoc.manager.storage.StorageException;
import java.time.OffsetDateTime;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Map<String, Object>> badRequest(Exception exception) {
        return response(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<Map<String, Object>> storageFailure(StorageException exception) {
        return response(HttpStatus.BAD_GATEWAY, exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> unexpectedFailure(Exception exception) {
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "The request could not be completed.");
    }

    private ResponseEntity<Map<String, Object>> response(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of(
                "timestamp", OffsetDateTime.now(),
                "status", status.value(),
                "message", message == null ? "The request is invalid." : message));
    }
}
