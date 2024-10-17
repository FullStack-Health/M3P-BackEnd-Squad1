package br.com.senai.medicalone.exceptions.customexceptions;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
