package br.com.senai.medicalone.exceptions.customexceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }

    public ResponseEntity<Object> toResponseEntity() {
        return new ResponseEntity<>(Map.of("message", getMessage()), HttpStatus.BAD_REQUEST);
    }
}