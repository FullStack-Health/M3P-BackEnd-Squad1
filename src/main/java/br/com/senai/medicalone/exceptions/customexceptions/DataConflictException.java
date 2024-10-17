package br.com.senai.medicalone.exceptions.customexceptions;

public class DataConflictException extends RuntimeException {
    public DataConflictException(String message) {
        super(message);
    }
}