package br.com.senai.medicalone.exceptions.customexceptions;

public class RecordNotFoundException extends RuntimeException {
    public RecordNotFoundException(String message) {
        super(message);
    }
}