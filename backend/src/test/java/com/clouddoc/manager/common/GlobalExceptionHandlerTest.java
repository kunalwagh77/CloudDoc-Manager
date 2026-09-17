package com.clouddoc.manager.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.clouddoc.manager.storage.StorageException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void badRequestReturnsClientErrorAndMessage() {
        ResponseEntity<java.util.Map<String, Object>> response = handler.badRequest(new IllegalArgumentException("bad input"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("bad input", response.getBody().get("message"));
        assertEquals(400, response.getBody().get("status"));
    }

    @Test
    void badRequestUsesDefaultMessageWhenExceptionHasNoMessage() {
        ResponseEntity<java.util.Map<String, Object>> response = handler.badRequest(new IllegalArgumentException());

        assertEquals("The request is invalid.", response.getBody().get("message"));
    }

    @Test
    void storageFailureReturnsBadGateway() {
        ResponseEntity<java.util.Map<String, Object>> response = handler.storageFailure(
                new StorageException("storage unavailable", new RuntimeException()));

        assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
        assertEquals("storage unavailable", response.getBody().get("message"));
    }

    @Test
    void unexpectedFailureHidesInternalDetails() {
        ResponseEntity<java.util.Map<String, Object>> response = handler.unexpectedFailure(
                new RuntimeException("private detail"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("The request could not be completed.", response.getBody().get("message"));
    }
}
